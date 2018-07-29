package ch.keepcalm.cloud.service.security.rest

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.security.core.context.SecurityContextHolder

class JwtTokenRelayHttpRequestInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        if (!request.headers.containsKey(AUTHORIZATION)) {
            SecurityContextHolder.getContext().authentication?.let {
                request.headers.set(AUTHORIZATION, "Bearer ${it.credentials}")
            }
        }
        return execution.execute(request, body)
    }
}
