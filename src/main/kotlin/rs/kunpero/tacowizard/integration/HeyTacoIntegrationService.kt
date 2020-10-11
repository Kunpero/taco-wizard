package rs.kunpero.tacowizard.integration

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import rs.kunpero.tacowizard.integration.dto.GiveTacoRequest
import rs.kunpero.tacowizard.integration.dto.GiveTacoResponse
import rs.kunpero.tacowizard.service.dto.FeedRequestDto
import rs.kunpero.tacowizard.service.dto.FeedResponseDto
import java.util.concurrent.locks.ReentrantLock

@Service
class HeyTacoIntegrationService(
    private val restTemplate: RestTemplate,
    private val methodsClient: MethodsClient,
    @Value("\${give.taco.url}")
    private var giveTacoUrl: String,
    @Value("\${taco.token}")
    private val tacoToken: String,
    @Value("\${notified.channel.id}")
    private val channelId: String,
    @Value("\${slack.access.token}")
    private val accessToken: String
) {
    companion object {
        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(HeyTacoIntegrationService::class.java)

        @JvmStatic
        private val LOCK = ReentrantLock()
    }

    fun feed(requestDto: FeedRequestDto): FeedResponseDto {
        val entity = HttpEntity(GiveTacoRequest(tacoToken, requestDto.toUser, requestDto.amount, requestDto.commentary))
        val response = restTemplate.postForObject(giveTacoUrl, entity, GiveTacoResponse::class.java)
        if ("true" == response!!.success) {
            notifySelectedChannel(requestDto)
            return FeedResponseDto(true, null)
        }
        return FeedResponseDto(false, response.error?.get(0))
    }

    private fun notifySelectedChannel(requestDto: FeedRequestDto) {
        try {
            LOCK.lock()
            val response = methodsClient.chatPostMessage(buildChatPostRequest(requestDto))
            log.debug(response.toString())
        } catch (e: Exception) {
            log.error("Error posting message to channel", e.printStackTrace())
        } finally {
            LOCK.unlock()
        }
    }

    private fun buildChatPostRequest(requestDto: FeedRequestDto): ChatPostMessageRequest? {
        return ChatPostMessageRequest.builder()
            .token(accessToken)
            .channel(channelId)
            .blocks(
                listOf(
                    SectionBlock.builder()
                        .text(
                            MarkdownTextObject.builder()
                                .text(
                                    String.format(
                                        "<@%s> %dx:taco: %s", requestDto.toUser,
                                        requestDto.amount, requestDto.commentary
                                    )
                                ).build()
                        ).build()
                )
            ).build()
    }
}