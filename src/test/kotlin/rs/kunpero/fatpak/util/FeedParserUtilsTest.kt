package rs.kunpero.fatpak.util

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload
import org.junit.Assert
import org.junit.Test
import rs.kunpero.fatpak.util.FeedParserUtils.Companion.parsePayload
import rs.kunpero.fatpak.util.exception.PayloadParserException

class FeedParserUtilsTest {
    @Test
    fun testCorrectTextWithCommentary() {
        val message = "@vrnsky 2 You are so cool!"
        val payload = SlashCommandPayload()
        payload.text = message
        val arr: List<String> = parsePayload(payload)
        Assert.assertEquals(3, arr.size.toLong())
        Assert.assertEquals("@vrnsky", arr[0])
        Assert.assertEquals("2", arr[1])
        Assert.assertEquals("You are so cool!", arr[2])
    }

    @Test
    fun testCorrectTextWithoutCommentary() {
        val message = "@vrnsky 2"
        val payload = SlashCommandPayload()
        payload.text = message
        val arr: List<String> = parsePayload(payload)
        Assert.assertEquals(2, arr.size.toLong())
        Assert.assertEquals("@vrnsky", arr[0])
        Assert.assertEquals("2", arr[1])
    }

    @Test(expected = PayloadParserException::class)
    fun textIsTooShortFailTest() {
        val message = "@vrnsky"
        val payload = SlashCommandPayload()
        payload.text = message
        parsePayload(payload)
    }

    @Test(expected = PayloadParserException::class)
    fun textIsEmptyFailTest() {
        val message = ""
        val payload = SlashCommandPayload()
        payload.text = message
        parsePayload(payload)
    }

    @Test(expected = PayloadParserException::class)
    fun textIsNullFailTest() {
        val payload = SlashCommandPayload()
        parsePayload(payload)
    }
}