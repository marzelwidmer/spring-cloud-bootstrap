package ch.keepcalm.cloud.service.foo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
// need to reach the bar-service, otherwise "java.lang.IllegalStateException: No instances available for bar-service"
public class FooServiceApplication {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FooServiceApplication.class, args);
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

@RestController
class FooController {

    private final BarClient barClient;

    public FooController(BarClient barClient) {
        this.barClient = barClient;
    }

    @GetMapping("/foobar")
    public ResponseEntity<String> fooBar() {
        return new ResponseEntity<>("foo " + barClient.getBar(), HttpStatus.OK);
    }



    @GetMapping("/")
    public ResponseEntity<String> foo() {
        return new ResponseEntity<>("foo ", HttpStatus.OK);
    }


}

@Component
class BarClient {

    private final RestTemplate restTemplate;

    public BarClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getBar() {
        return restTemplate.getForObject("http://bar-service", String.class);
    }
}