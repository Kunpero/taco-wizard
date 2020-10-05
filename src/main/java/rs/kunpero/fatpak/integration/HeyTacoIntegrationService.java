package rs.kunpero.fatpak.integration;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import rs.kunpero.fatpak.integration.dto.GiveTacoRequest;
import rs.kunpero.fatpak.integration.dto.GiveTacoResponse;
import rs.kunpero.fatpak.service.dto.FeedRequestDto;
import rs.kunpero.fatpak.service.dto.FeedResponseDto;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class HeyTacoIntegrationService {
    private static final ReentrantLock LOCK = new ReentrantLock();

    private final RestTemplate restTemplate;
    private final MethodsClient methodsClient;

    @Value("${give.taco.url}")
    private String giveTacoUrl;
    @Value("${taco.token}")
    private String tacoToken;
    @Value("${notified.channel.id}")
    private String channelId;
    @Value("${slack.access.token}")
    private String accessToken;

    public HeyTacoIntegrationService(RestTemplate restTemplate, MethodsClient methodsClient) {
        this.restTemplate = restTemplate;
        this.methodsClient = methodsClient;
    }

    public FeedResponseDto feed(FeedRequestDto requestDto) {
        HttpEntity<GiveTacoRequest> entity = new HttpEntity<>(new GiveTacoRequest(tacoToken, requestDto.getToUser(),
                requestDto.getAmount(), requestDto.getCommentary()));

        GiveTacoResponse response = restTemplate.postForObject(giveTacoUrl, entity, GiveTacoResponse.class);
        Assert.notNull(response, "response is null");
        if ("true".equals(response.getSuccess())) {
            notifySelectedChannel(requestDto);
            return new FeedResponseDto().setSuccess(true);
        }
        return new FeedResponseDto()
                .setSuccess(false)
                .setErrorMessage(response.getError().get(0));
    }

    private void notifySelectedChannel(FeedRequestDto requestDto) {
        try {
            LOCK.lock();
            ChatPostMessageResponse response = methodsClient
                    .chatPostMessage(buildChatPostRequest(requestDto));
            log.debug(response.toString());
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
    }

    private ChatPostMessageRequest buildChatPostRequest(FeedRequestDto requestDto) {
        return ChatPostMessageRequest.builder()
                .token(accessToken)
                .channel(channelId)
                .blocks(List.of(
                        SectionBlock.builder()
                                .text(MarkdownTextObject.builder()
                                        .text(String.format("<@%s> %dx:taco: %s", requestDto.getToUser(),
                                                requestDto.getAmount(), requestDto.getCommentary()))
                                        .build())
                                .build()))
                .build();
    }
}
