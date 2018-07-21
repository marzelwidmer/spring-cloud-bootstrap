package ch.keepcalm.cloud.service.security.rest

import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.web.client.RestTemplate

class JwtTokenRelayRestTemplateCustomizer : RestTemplateCustomizer {
    override fun customize(restTemplate: RestTemplate) {
        restTemplate.interceptors.add(JwtTokenRelayHttpRequestInterceptor())
    }
}