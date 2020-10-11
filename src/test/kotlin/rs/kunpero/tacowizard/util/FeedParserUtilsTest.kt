package rs.kunpero.tacowizard.util

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload
import org.junit.Assert
import org.junit.Test
import rs.kunpero.tacowizard.util.FeedParserUtils.Companion.parsePayload
import rs.kunpero.tacowizard.util.exception.PayloadParserException

class FeedParserUtilsTest {
    @Test
    fun `Correct text with commentary`() {
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
    fun `Correct text without commentary`() {
        val message = "@vrnsky 2"
        val payload = SlashCommandPayload()
        payload.text = message
        val arr: List<String> = parsePayload(payload)
        Assert.assertEquals(2, arr.size.toLong())
        Assert.assertEquals("@vrnsky", arr[0])
        Assert.assertEquals("2", arr[1])
    }

    @Test(expected = PayloadParserException::class)
    fun `Argument list is too short`() {
        val message = "@vrnsky"
        val payload = SlashCommandPayload()
        payload.text = message
        parsePayload(payload)
    }

    @Test(expected = PayloadParserException::class)
    fun `Text is empty`() {
        val message = ""
        val payload = SlashCommandPayload()
        payload.text = message
        parsePayload(payload)
    }

    @Test(expected = PayloadParserException::class)
    fun `Text is null`() {
        val payload = SlashCommandPayload()
        parsePayload(payload)
    }
}