package ch.keepcalm.cloud.service.security.rest

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder

class JwtTokenRelayFeignRequestInterceptor : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        SecurityContextHolder.getContext().authentication?.let {
            template.header(HttpHeaders.AUTHORIZATION, "Bearer $it.credentials")
        }
    }
}