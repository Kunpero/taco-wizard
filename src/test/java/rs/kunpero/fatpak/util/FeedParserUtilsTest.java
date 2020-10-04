package rs.kunpero.fatpak.util;

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import org.junit.Assert;
import org.junit.Test;
import rs.kunpero.fatpak.util.exception.PayloadParserException;

public class FeedParserUtilsTest {
    @Test
    public void testCorrectTextWithCommentary() {
        final String message = "@vrnsky 2 You are so cool!";
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setText(message);
        String[] arr = FeedParserUtils.parsePayload(payload);
        Assert.assertEquals(3, arr.length);
        Assert.assertEquals("@vrnsky", arr[0]);
        Assert.assertEquals("2", arr[1]);
        Assert.assertEquals("You are so cool!", arr[2]);

    }

    @Test
    public void testCorrectTextWithoutCommentary() {
        final String message = "@vrnsky 2";
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setText(message);
        String[] arr = FeedParserUtils.parsePayload(payload);
        Assert.assertEquals(2, arr.length);
        Assert.assertEquals("@vrnsky", arr[0]);
        Assert.assertEquals("2", arr[1]);

    }

    @Test(expected = PayloadParserException.class)
    public void textIsTooShortFailTest() {
        final String message = "@vrnsky";
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setText(message);
        FeedParserUtils.parsePayload(payload);
    }

    @Test(expected = PayloadParserException.class)
    public void textIsEmptyFailTest() {
        final String message = "";
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setText(message);
        FeedParserUtils.parsePayload(payload);
    }

    @Test(expected = PayloadParserException.class)
    public void textIsNullFailTest() {
        SlashCommandPayload payload = new SlashCommandPayload();
        FeedParserUtils.parsePayload(payload);
    }
}
