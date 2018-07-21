package ch.keepcalm.cloud.service.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.charset.StandardCharsets
import java.util.*

@ConfigurationProperties("spring.security.jwt")
class JwtSecurityProperties {

    lateinit var issuer: String
    lateinit var audience: String
    lateinit var roles: String

    var secret: String = ""
        set(value) {
            field = Base64.getEncoder().encodeToString(value.toByteArray(StandardCharsets.UTF_8))
        }
}