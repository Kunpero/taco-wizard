package rs.kunpero.tacowizard.api

import com.slack.api.app_backend.slash_commands.SlashCommandPayloadParser
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import rs.kunpero.tacowizard.integration.HeyTacoIntegrationService
import rs.kunpero.tacowizard.service.PayloadParserService
import rs.kunpero.tacowizard.util.MessageSourceHelper
import rs.kunpero.tacowizard.util.exception.PayloadParserException
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
        private val log: Logger = LoggerFactory.getLogger(FeedController::class.java)

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
        return try {
            val requestDto = payloadParserService.parse(payload)
            val responseDto = heyTacoIntegrationService.feed(requestDto)
            if (responseDto.isSuccessful) {
                buildResponse("success")
            } else buildResponse(responseDto.errorMessage)
        } catch (ex: PayloadParserException) {
            buildResponse(ex.message)
        } catch (ex: NumberFormatException) {
            buildResponse("incorrect.amount.format")
        } catch (ex: Exception) {
            log.error("System error", ex)
            buildResponse("system.error")
        }
    }

    private fun buildResponse(message: String?): SlashCommandResponse {
        return SlashCommandResponse.builder()
            .responseType("ephemeral")
            .blocks(
                listOf(
                    SectionBlock.builder()
                        .text(
                            MarkdownTextObject.builder()
                                .text(messageSourceHelper.getMessage(message!!, null)).build()
                        ).build()
                )
            ).build()
    }
}