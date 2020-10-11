package rs.kunpero.fatpak.config

import com.slack.api.app_backend.events.servlet.SlackSignatureVerifier
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.RequestMatcher
import rs.kunpero.fatpak.config.filter.SlackRequestVerifierFilter
import javax.servlet.http.HttpServletRequest

@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .mvcMatchers("/feed/**")
            .permitAll()
            .and()
            .requiresChannel()
            .requestMatchers(RequestMatcher { r: HttpServletRequest ->
                r.getHeader(
                    "X-Forwarded-Proto"
                ) != null
            })
            .requiresSecure()
            .and().csrf().disable()
    }

    @Bean
    fun slackRequestVerifierFilter(slackSignatureVerifier: SlackSignatureVerifier): SlackRequestVerifierFilter {
        return SlackRequestVerifierFilter(slackSignatureVerifier)
    }

    @Bean
    fun slackRequestVerifierFilterRegistration(filter: SlackRequestVerifierFilter): FilterRegistrationBean<SlackRequestVerifierFilter> {
        val registration: FilterRegistrationBean<SlackRequestVerifierFilter> = FilterRegistrationBean()
        registration.filter = filter
        registration.addUrlPatterns("/feed/*")
        registration.order = Ordered.HIGHEST_PRECEDENCE
        return registration
    }
}