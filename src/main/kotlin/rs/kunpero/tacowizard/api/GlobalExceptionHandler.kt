package rs.kunpero.tacowizard.api

import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import rs.kunpero.tacowizard.util.ApiUtills.Companion.buildResponse
import rs.kunpero.tacowizard.util.MessageSourceHelper
import rs.kunpero.tacowizard.util.exception.PayloadParserException

@ControllerAdvice
class GlobalExceptionHandler(
    private val messageSourceHelper: MessageSourceHelper
) {

    companion object {
        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(FeedController::class.java)
    }

    @ExceptionHandler(PayloadParserException::class)
    fun handlePayloadParserException(ex: PayloadParserException): SlashCommandResponse {
        return buildResponse(messageSourceHelper.getMessage(ex.message!!, null))
    }

    @ExceptionHandler(NumberFormatException::class)
    fun handleNumberFormatException(ex: NumberFormatException): SlashCommandResponse {
        return buildResponse(messageSourceHelper.getMessage("incorrect.amount.format", null))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: NumberFormatException): SlashCommandResponse {
        log.error("System error", ex)
        return buildResponse(messageSourceHelper.getMessage("system.error", null))
    }
}