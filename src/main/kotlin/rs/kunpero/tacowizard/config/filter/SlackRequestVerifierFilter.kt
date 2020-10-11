package rs.kunpero.tacowizard.config.filter

import com.slack.api.app_backend.SlackSignature
import com.slack.api.app_backend.events.servlet.SlackSignatureVerifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.filter.GenericFilterBean
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SlackRequestVerifierFilter(
    private val slackSignatureVerifier: SlackSignatureVerifier
) : GenericFilterBean() {
    companion object {
        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(SlackRequestVerifierFilter::class.java)
    }

    override fun doFilter(
        servletRequest: ServletRequest,
        servletResponse: ServletResponse,
        filterChain: FilterChain
    ) {
        val wrappedRequest =
            AuthenticationRequestWrapper((servletRequest as HttpServletRequest))
        val body = wrappedRequest.reader.lines().collect(Collectors.joining())
        val isValid = slackSignatureVerifier.isValid(wrappedRequest, body)
        if (!isValid) { // invalid signature
            val signature =
                servletRequest.getHeader(SlackSignature.HeaderNames.X_SLACK_SIGNATURE)
            log.debug("An invalid X-Slack-Signature detected - {}", signature)
            (servletResponse as HttpServletResponse).status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }
        filterChain.doFilter(wrappedRequest, servletResponse)
    }
}