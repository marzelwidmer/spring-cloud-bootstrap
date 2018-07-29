package ch.keepcalm.cloud.service.security.rest

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.context.SecurityContextHolder

class JwtTokenRelayFeignRequestInterceptor : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        if (!template.headers().containsKey(AUTHORIZATION)) {
            SecurityContextHolder.getContext().authentication?.let {
                template.header(AUTHORIZATION, "Bearer ${it.credentials}")
            }
        }
    }
}
