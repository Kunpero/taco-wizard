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
open class SlackConfig (
    @Value("\${slack.signing.secret}")
    private val signingSecret: String,
    @Value("\${slack.access.token}")
    private val accessToken: String
){
    @Bean
    open fun slack(): Slack {
        return Slack.getInstance()
    }

    @Bean
    open fun methodsClient(): MethodsClient {
        return slack().methods(accessToken)
    }

    @Bean
    open fun responseSender(slack: Slack): ActionResponseSender {
        return ActionResponseSender(slack)
    }

    @Bean
    open fun gsonHttpMessageConverter(gson: Gson): GsonHttpMessageConverter {
        val converter = GsonHttpMessageConverter()
        converter.gson = gson
        return converter
    }

    @Bean
    open fun gson(): Gson {
        return GsonFactory.createSnakeCase()
    }

    @Bean
    open fun slackSignatureGenerator(): SlackSignature.Generator {
        return SlackSignature.Generator(signingSecret)
    }

    @Bean
    open fun slackSignatureVerifier(slackSignatureGenerator: SlackSignature.Generator): SlackSignatureVerifier {
        return SlackSignatureVerifier(slackSignatureGenerator)
    }

    @Bean
    open fun actionResponseSender(slack: Slack): ActionResponseSender {
        return ActionResponseSender(slack)
    }

    @Bean
    open fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}