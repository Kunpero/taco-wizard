package rs.kunpero.tacowizard.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageSourceHelper(
    private val messageSource: MessageSource,
    @Value("\${default.locale}")
    private val defaultLocale: String
) {
    fun getCode(source: String): Int {
        val code = messageSource.getMessage("$source.code", null, Locale(defaultLocale))
        return code.toInt()
    }

    fun getMessage(source: String, @Nullable args: Array<String?>?): String {
        return messageSource.getMessage("$source.message", args, Locale(defaultLocale))
    }
}