package rs.kunpero.fatpak.api;

import com.slack.api.app_backend.slash_commands.SlashCommandPayloadParser;
import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rs.kunpero.fatpak.integration.HeyTacoIntegrationService;
import rs.kunpero.fatpak.service.PayloadParserService;
import rs.kunpero.fatpak.service.dto.FeedRequestDto;
import rs.kunpero.fatpak.service.dto.FeedResponseDto;
import rs.kunpero.fatpak.util.MessageSourceHelper;
import rs.kunpero.fatpak.util.exception.PayloadParserException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@Slf4j
@RequestMapping("/feed")
@AllArgsConstructor
public class FeedController {
    private static final SlashCommandPayloadParser SLASH_COMMAND_PAYLOAD_PARSER = new SlashCommandPayloadParser();

    private final PayloadParserService payloadParserService;
    private final HeyTacoIntegrationService heyTacoIntegrationService;
    private final MessageSourceHelper messageSourceHelper;

    @RequestMapping(value = "/start", method = RequestMethod.POST, consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public SlashCommandResponse start(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining());
        body = URLDecoder.decode(body, StandardCharsets.UTF_8);

        SlashCommandPayload payload = SLASH_COMMAND_PAYLOAD_PARSER.parse(body);
        try {
            FeedRequestDto requestDto = payloadParserService.parse(payload);
            FeedResponseDto responseDto = heyTacoIntegrationService.feed(requestDto);
            if (responseDto.isSuccess()) {
                return buildResponse("success");
            }
            return buildResponse(responseDto.getErrorMessage());
        } catch (PayloadParserException ex) {
            return buildResponse(ex.getMessage());
        } catch (NumberFormatException ex) {
            return buildResponse("incorrect.amount.format");
        } catch (Exception ex) {
            log.error("System error", ex);
            return buildResponse("system.error");
        }
    }

    private SlashCommandResponse buildResponse(String message) {
        return SlashCommandResponse.builder()
                .responseType("ephemeral")
                .blocks(List.of(
                        SectionBlock.builder()
                                .text(MarkdownTextObject.builder()
                                        .text(messageSourceHelper.getMessage(message, null))
                                        .build())
                                .build()))
                .build();
    }
}
