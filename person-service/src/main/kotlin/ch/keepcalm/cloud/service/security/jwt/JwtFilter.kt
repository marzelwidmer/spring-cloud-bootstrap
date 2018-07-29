package ch.keepcalm.cloud.service.security.jwt

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils.hasText
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


class JwtFilter(private val tokenVerifier: JwtTokenVerifier) : GenericFilterBean() {

    companion object {
        const val AUTH_HEADER_NAME_ADMIN = "X-Authorization"
        const val AUTHENTICATION_SCHEMA = "Bearer "
    }

    private val LOG = LoggerFactory.getLogger(JwtFilter::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {

        authenticateIfAvailable(resolveToken(servletRequest as HttpServletRequest))
        filterChain.doFilter(servletRequest, servletResponse)
    }

    private fun authenticateIfAvailable(jwt: String?) {
        if (hasText(jwt)) {
            if (tokenVerifier.validateToken(jwt!!)) {
                SecurityContextHolder.getContext().authentication = tokenVerifier.getAuthentication(jwt)
            }
        }
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        LOG.trace("$AUTHORIZATION=${request.getHeader(AUTHORIZATION)}")
        LOG.trace("$AUTH_HEADER_NAME_ADMIN=${request.getHeader(AUTH_HEADER_NAME_ADMIN)}")

        val bearerToken = request.getHeader(AUTHORIZATION) ?: request.getHeader(AUTH_HEADER_NAME_ADMIN)
        LOG.trace("Found Authorization Header: $bearerToken")

        return toJwtToken(bearerToken)
    }

    private fun toJwtToken(bearerToken: String?): String? {
        if (bearerToken != null && bearerToken.startsWith(AUTHENTICATION_SCHEMA)) {
            return bearerToken.substring(AUTHENTICATION_SCHEMA.length, bearerToken.length).trim()
                    .also { LOG.debug("Found JWT token: $it") }
        }
        return null
    }
}
