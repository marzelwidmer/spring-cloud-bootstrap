package ch.keepcalm.cloud.service.person

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@SpringBootApplication
class PersonService {

    @Configuration
    @Profile("unsecure")
    @EnableWebSecurity
    internal class WebSecurityConfig : WebSecurityConfigurerAdapter() {
        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            // https://stackoverflow.com/questions/48902706/spring-cloud-eureka-with-spring-security/
            http.csrf().disable()
                    .authorizeRequests()
                    .anyRequest().permitAll()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<PersonService>(*args)
}