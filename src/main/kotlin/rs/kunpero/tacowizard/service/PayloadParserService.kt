package rs.kunpero.tacowizard.service

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload
import com.slack.api.methods.SlackApiException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import rs.kunpero.tacowizard.service.dto.FeedRequestDto
import rs.kunpero.tacowizard.util.FeedParserUtils.Companion.parsePayload
import rs.kunpero.tacowizard.util.exception.PayloadParserException
import java.io.IOException

@Service
class PayloadParserService(
    private val userCacheService: UserCacheService
) {
    companion object {
        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(PayloadParserService::class.java)
    }

    @Throws(PayloadParserException::class, NumberFormatException::class, IOException::class, SlackApiException::class)
    fun parse(payload: SlashCommandPayload): FeedRequestDto {
        log.info("Incoming text: [{}]", payload.text)
        val parsedText: List<String> = parsePayload(payload)
        val user = userCacheService.getUser(payload.userName)

        if (!user!!.isAdmin && !user.isOwner) {
            log.error("Only admin/owner can use this command")
            throw PayloadParserException("action.not.permitted")
        }
        val fromUserId = payload.userId
        val toUserId = userCacheService.getUserIdFromCache(parsedText[0])
        if (toUserId == null) {
            log.error("slack userId for [{}] not found", parsedText[0])
            throw PayloadParserException("user.not.found")
        }
        val amount = parsedText[1].toInt()
        if (amount <= 0) {
            throw PayloadParserException("wrong.amount.format")
        }
        val commentary = parsedText[2]
        return FeedRequestDto(fromUserId, toUserId, amount, commentary)
    }
}