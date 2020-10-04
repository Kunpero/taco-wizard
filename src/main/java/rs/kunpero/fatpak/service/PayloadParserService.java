package rs.kunpero.fatpak.service;

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.methods.SlackApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rs.kunpero.fatpak.dto.FeedRequestDto;
import rs.kunpero.fatpak.util.exception.PayloadParserException;

import java.io.IOException;

import static rs.kunpero.fatpak.util.FeedParserUtils.parsePayload;

@Service
@Slf4j
public class PayloadParserService {

    private final UserCacheService userCacheService;

    public PayloadParserService(UserCacheService userCacheService) {
        this.userCacheService = userCacheService;
    }

    public FeedRequestDto parse(final SlashCommandPayload payload) throws PayloadParserException, NumberFormatException,
            IOException, SlackApiException {
        log.info("Incoming text: [{}]", payload.getText());
        final String[] parsedText = parsePayload(payload);

        final String fromUserId = payload.getUserId();
        final String toUserId = userCacheService.getUserIdFromCache(parsedText[0]);

        if (toUserId == null) {
            log.error("slack userId for [{}] not found", parsedText[0]);
            throw new PayloadParserException("user.not.found");
        }

        final int amount = Integer.parseInt(parsedText[1]);
        if (amount <= 0) {
            throw new PayloadParserException("wrong.amount.format");
        }
        final String commentary = parsedText[2];
        return new FeedRequestDto(fromUserId, toUserId, amount, commentary);
    }
}
