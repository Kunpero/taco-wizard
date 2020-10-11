package rs.kunpero.fatpak.util

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rs.kunpero.fatpak.util.exception.PayloadParserException

class FeedParserUtils {
    companion object {
        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(FeedParserUtils::class.java)

        @JvmStatic
        fun parsePayload(payload: SlashCommandPayload): List<String> {
            val text = payload.text
            if (text.isNullOrEmpty()) {
                log.error("Empty payload text")
                throw PayloadParserException("empty.payload.text")
            }

            val list = text.split(" ".toRegex(), 3)
            if (list.size !in 2..3) {
                log.error("Not enough arguments")
                throw PayloadParserException("not.enough.arguments")
            }
            log.info("Parsed array: username:[{}], amount:[{}], commentary:[{}]",
                list[0],
                list[1],
                list.takeIf { list.size == 3 }?.apply { list[2] }
            )
            return list
        }
    }
}