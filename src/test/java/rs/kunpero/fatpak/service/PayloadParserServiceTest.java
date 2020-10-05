package rs.kunpero.fatpak.service;

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rs.kunpero.fatpak.service.dto.FeedRequestDto;
import rs.kunpero.fatpak.util.exception.PayloadParserException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserCacheService.class, PayloadParserService.class})
public class PayloadParserServiceTest {
    @MockBean
    private UserCacheService userCacheService;

    @Autowired
    private PayloadParserService payloadParserService;

    @Test(expected = PayloadParserException.class)
    public void noUserIdInTheWorkspaceFailTest() throws IOException, SlackApiException {
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setUserName("admin");
        payload.setText("@user4 3");

        when(userCacheService.getUser(anyString())).thenReturn(buildUser(true));
        when(userCacheService.getUserIdFromCache(anyString())).thenReturn(null);
        payloadParserService.parse(payload);
    }

    @Test
    public void userInCacheSuccessTest() throws IOException, SlackApiException {
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setUserId("user0Id");
        payload.setUserName("admin");
        payload.setText("@user4 3 test test test");

        when(userCacheService.getUser(anyString())).thenReturn(buildUser(true));
        when(userCacheService.getUserIdFromCache(anyString())).thenReturn("user4Id");
        FeedRequestDto requestDto = payloadParserService.parse(payload);
        Assert.assertEquals("user0Id", requestDto.getFromUser());
        Assert.assertEquals("user4Id", requestDto.getToUser());
        Assert.assertEquals("test test test", requestDto.getCommentary());
        Assert.assertEquals(3, requestDto.getAmount());
    }


    @Test(expected = NumberFormatException.class)
    public void incorrectAmountFormatFailTest() throws IOException, SlackApiException {
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setText("@user4 pepe");
        payload.setUserName("admin");

        when(userCacheService.getUser(anyString())).thenReturn(buildUser(true));
        when(userCacheService.getUserIdFromCache(anyString())).thenReturn("user4Id");
        payloadParserService.parse(payload);
    }

    @Test(expected = PayloadParserException.class)
    public void zeroAmountFailTest() throws IOException, SlackApiException {
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setText("@user4 0");
        payload.setUserName("admin");

        when(userCacheService.getUser(anyString())).thenReturn(buildUser(true));
        when(userCacheService.getUserIdFromCache(anyString())).thenReturn("user4Id");
        payloadParserService.parse(payload);
    }

    @Test(expected = PayloadParserException.class)
    public void negativeAmountFailTest() throws IOException, SlackApiException {
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setText("@user4 -1");
        payload.setUserName("admin");

        when(userCacheService.getUser(anyString())).thenReturn(buildUser(true));
        when(userCacheService.getUserIdFromCache(anyString())).thenReturn("user4Id");
        payloadParserService.parse(payload);
    }

    @Test(expected = PayloadParserException.class)
    public void commandNotPermittedFailTest() throws IOException, SlackApiException {
        SlashCommandPayload payload = new SlashCommandPayload();
        payload.setText("@user4 -1");
        payload.setUserName("notAdmin");

        when(userCacheService.getUser(anyString())).thenReturn(buildUser(false));
        when(userCacheService.getUserIdFromCache(anyString())).thenReturn("user4Id");
        payloadParserService.parse(payload);
    }

    private User buildUser(boolean isAdmin) {
        User user = new User();
        user.setAdmin(isAdmin);
        return user;
    }
}