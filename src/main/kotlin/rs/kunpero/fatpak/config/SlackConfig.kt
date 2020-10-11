package rs.kunpero.fatpak.config

import com.google.gson.Gson
import com.slack.api.Slack
import com.slack.api.app_backend.SlackSignature
import com.slack.api.app_backend.events.servlet.SlackSignatureVerifier
import com.slack.api.app_backend.interactive_components.ActionResponseSender
import com.slack.api.methods.MethodsClient
import com.slack.api.util.json.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class SlackConfig (
    @Value("\${slack.signing.secret}")
    private val signingSecret: String,
    @Value("\${slack.access.token}")
    private val accessToken: String
){
    @Bean
    fun slack(): Slack {
        return Slack.getInstance()
    }

    @Bean
    fun methodsClient(): MethodsClient {
        return slack().methods(accessToken)
    }

    @Bean
    fun responseSender(slack: Slack): ActionResponseSender {
        return ActionResponseSender(slack)
    }

    @Bean
    fun gsonHttpMessageConverter(gson: Gson): GsonHttpMessageConverter {
        val converter = GsonHttpMessageConverter()
        converter.gson = gson
        return converter
    }

    @Bean
    fun gson(): Gson {
        return GsonFactory.createSnakeCase()
    }

    @Bean
    fun slackSignatureGenerator(): SlackSignature.Generator {
        return SlackSignature.Generator(signingSecret)
    }

    @Bean
    fun slackSignatureVerifier(slackSignatureGenerator: SlackSignature.Generator): SlackSignatureVerifier {
        return SlackSignatureVerifier(slackSignatureGenerator)
    }

    @Bean
    fun actionResponseSender(slack: Slack): ActionResponseSender {
        return ActionResponseSender(slack)
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}