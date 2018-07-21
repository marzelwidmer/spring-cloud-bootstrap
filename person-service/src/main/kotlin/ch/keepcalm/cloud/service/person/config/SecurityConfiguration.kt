package ch.keepcalm.cloud.service.person.config

import ch.keepcalm.cloud.service.security.jwt.JwtFilter
import ch.keepcalm.cloud.service.security.jwt.JwtSecurityProperties
import ch.keepcalm.cloud.service.security.jwt.JwtTokenVerifier
import ch.keepcalm.cloud.service.security.rest.JwtTokenRelayFeignRequestInterceptor
import ch.keepcalm.cloud.service.security.rest.JwtTokenRelayRestTemplateCustomizer
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfiguration {

    @Profile("unsecure")
    @Order(100)
    @Configuration
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

    @Profile("secure")
    @Order(90)
    @Configuration
    @EnableConfigurationProperties(value = [JwtSecurityProperties::class, SecurityProperties::class])
    internal class SecurityJwtConfig(
            private val jwtSecurityProperties: JwtSecurityProperties,
            private val securityProperties: SecurityProperties) : WebSecurityConfigurerAdapter() {

//        companion object {
//            const val ROLE_MYHELSANA_USER = "keepcalm.user"
//        }

        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {

            http
                    // disable default security
                    .httpBasic().and()
                    .formLogin().disable()
                    .logout().disable()
                    .csrf().disable()
                    .addFilterBefore(JwtFilter(JwtTokenVerifier(jwtSecurityProperties)), UsernamePasswordAuthenticationFilter::class.java)
                    .exceptionHandling().authenticationEntryPoint(Http403ForbiddenEntryPoint()).and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .securityContext().disable()

                    .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .antMatchers("/docs/**").permitAll()
//                    .antMatchers("/api/**").hasAuthority(Companion.ROLE_MYHELSANA_USER)
                    .antMatchers("/api/**").hasAuthority(jwtSecurityProperties.roles.toString())
                    .requestMatchers(EndpointRequest.to(HealthEndpoint::class.java, InfoEndpoint::class.java)).permitAll()
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(securityProperties.user.roles.first())
        }

        @Bean
        fun jwtRestTemplateCustomizer() = JwtTokenRelayRestTemplateCustomizer()

        @Bean
        fun jwtTokenRelayFeignRequestInterceptor() = JwtTokenRelayFeignRequestInterceptor()

    }
}