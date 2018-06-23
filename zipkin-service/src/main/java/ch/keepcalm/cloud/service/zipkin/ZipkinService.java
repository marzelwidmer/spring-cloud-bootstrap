package ch.keepcalm.cloud.service.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import zipkin.server.internal.EnableZipkinServer;


@SpringBootApplication
@EnableEurekaClient
@EnableZipkinServer
public class ZipkinService {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinService.class, args);
    }

    @Configuration
    @Profile("unsecure")
    @EnableWebSecurity
    static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // https://stackoverflow.com/questions/48902706/spring-cloud-eureka-with-spring-security/
            http.csrf().disable()
                    .authorizeRequests()
                    .anyRequest().permitAll();
        }
    }

}
