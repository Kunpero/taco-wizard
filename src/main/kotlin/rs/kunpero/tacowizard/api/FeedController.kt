package rs.kunpero.tacowizard.api

import com.slack.api.app_backend.slash_commands.SlashCommandPayloadParser
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import rs.kunpero.tacowizard.integration.HeyTacoIntegrationService
import rs.kunpero.tacowizard.service.PayloadParserService
import rs.kunpero.tacowizard.util.ApiUtills.Companion.buildResponse
import rs.kunpero.tacowizard.util.MessageSourceHelper
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/feed")
class FeedController(
    private val payloadParserService: PayloadParserService,
    private val heyTacoIntegrationService: HeyTacoIntegrationService,
    private val messageSourceHelper: MessageSourceHelper
) {
    companion object {
        @JvmStatic
        private val SLASH_COMMAND_PAYLOAD_PARSER: SlashCommandPayloadParser = SlashCommandPayloadParser()
    }

    @RequestMapping(
        value = ["/start"],
        method = [RequestMethod.POST],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun start(request: HttpServletRequest): SlashCommandResponse {
        val body = URLDecoder.decode(request.reader.lines().collect(Collectors.joining()), StandardCharsets.UTF_8)
        val payload = SLASH_COMMAND_PAYLOAD_PARSER.parse(body)
        val requestDto = payloadParserService.parse(payload)
        val responseDto = heyTacoIntegrationService.feed(requestDto)
        return if (responseDto.isSuccessful) {
            buildResponse(messageSourceHelper.getMessage("success", null))
        } else {
            buildResponse(messageSourceHelper.getMessage(responseDto.errorMessage!!, null))
        }
    }
}