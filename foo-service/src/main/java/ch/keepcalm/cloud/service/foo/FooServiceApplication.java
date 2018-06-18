package ch.keepcalm.cloud.service.foo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
class GreetingController {

    private final BarClient barClient;

    public GreetingController(BarClient barClient){
        this.barClient = barClient;
    }

    private static final String TEMPLATE = "Hello, %s!";

    @RequestMapping("/greeting")
    public HttpEntity<Greeting> greeting(
            @RequestParam(value = "name", required = false, defaultValue = "FooService") String name) {

        Greeting greeting = new Greeting(String.format(TEMPLATE, name));
        greeting.add(linkTo(methodOn(GreetingController.class).greeting(name)).withSelfRel());

        return new ResponseEntity<>(greeting, HttpStatus.OK);
    }

    @GetMapping("/greetingbar")
    public ResponseEntity<String> fooMeetBar() {
        Greeting bar = barClient.getBar();
        System.out.println(bar.getContent());
        return new ResponseEntity<>(bar.getContent(), HttpStatus.OK);
    }

}

class Greeting extends ResourceSupport {

    private final String content;

    @JsonCreator
    public Greeting(@JsonProperty("content") String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

@Component
class BarClient {

    @LoadBalanced
    private final RestTemplate restTemplate;

    public BarClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Greeting getBar() {
        return restTemplate.getForObject("http://bar-service" + "/greeting", Greeting.class);
    }
}