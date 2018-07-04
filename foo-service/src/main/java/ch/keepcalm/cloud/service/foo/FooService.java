package ch.keepcalm.cloud.service.foo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.hypermedia.DiscoveredResource;
import org.springframework.cloud.client.hypermedia.DynamicServiceInstanceProvider;
import org.springframework.cloud.client.hypermedia.ServiceInstanceProvider;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.*;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@SpringBootApplication
@EnableDiscoveryClient
public class FooService {

    /**
     * A remote {@link DiscoveredResource} that provides functionality to lookup foods.
     *
     * @param provider
     * @return
     */
    @Bean
    public DiscoveredResource foodsResource(ServiceInstanceProvider provider) {
        return new DiscoveredResource(provider, traverson -> traverson.follow("foods"));
    }


    @Bean
    public DynamicServiceInstanceProvider dynamicServiceProvider(DiscoveryClient client) {
        return new DynamicServiceInstanceProvider(client, "nutrition-service");
    }

    @LoadBalanced
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FooService.class, args);
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


@Slf4j
@RestController
class GreetingController {

    private final BarClient barClient;
    private final DiscoveredResource foodsResource;

    public GreetingController(BarClient barClient, DiscoveredResource foodsResource) {
        this.foodsResource = foodsResource;
        this.barClient = barClient;
    }

    private static final String TEMPLATE = "Hello, %s!";

    @RequestMapping("/greeting")
    public HttpEntity<Greeting> greeting(
            @RequestParam(value = "name", required = false, defaultValue = "FooService") String name) {

        Greeting greeting = new Greeting(String.format(TEMPLATE, name));
        greeting.add(linkTo(methodOn(GreetingController.class).greeting(name)).withSelfRel());
        log.info(greeting.getContent());
        return new ResponseEntity<>(greeting, HttpStatus.OK);
    }

    @GetMapping("/greetingbar")
    public ResponseEntity<String> fooMeetBar() {
        Greeting bar = barClient.getBar();
        log.info(bar.getContent());
        return new ResponseEntity<>(bar.getContent(), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<String> getFoodWithTraverson(
            @RequestParam(value = "name", required = false, defaultValue = "*") String name) throws URISyntaxException {

        final String REMOTE_SERVICE_ROOT_URI = foodsResource.getProvider().getServiceInstance().getUri().toString();


        TypeReferences.ResourcesType<Resource<Food>> resourceParameterizedTypeReference =
                new TypeReferences.ResourcesType<Resource<Food>>() {};

        URI apiBaseUrl = foodsResource.getProvider().getServiceInstance().getUri();

        Traverson traverson = new Traverson(apiBaseUrl, MediaTypes.HAL_JSON);
        Traverson.TraversalBuilder builder = traverson.follow("foods", "find");

//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", String.format("Bearer %s", generateJsonWebToken(toolId)));
//        builder.withHeaders(headers);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);

        Resources<Resource<Food>> associationResources = builder.withTemplateParameters(parameters).toObject(resourceParameterizedTypeReference);
        // Should only be one matching this search criterion
        if (associationResources.getContent().size() > 1) {
            throw new IllegalStateException(String.format(
                    "Number of rubric association resources greater than one for request: %s",
                    associationResources.getLink(Link.REL_SELF).toString()));
        }

        Optional<Resource<Food>> firstAssociationResources = associationResources.getContent().stream().findFirst();
        return new ResponseEntity(firstAssociationResources, HttpStatus.OK);


/*        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);

        Traverson traverson = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON, MediaTypes.HAL_JSON_UTF8);

        ParameterizedTypeReference<Resource<Food>> resourceParameterizedTypeReference = new ParameterizedTypeReference<Resource<Food>>() {
        };*/



        //        Food food = traverson.
//                follow(rel("foods").withParameters(parameters)).//
//                follow("$._embedded.foods[0]._links.self.href").//
//                toObject(resourceParameterizedTypeReference).getContent();

/*

        Food food = traverson
                .follow("foods", "find")
                .withTemplateParameters(parameters)
                .toObject(resourceParameterizedTypeReference)
                .getContent();
*/


        //        Traverson traverson = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI),
//                MediaTypes.HAL_JSON, MediaTypes.HAL_JSON_UTF8); // .setRestOperations(restTemplate);
//        Resources<Resource<Food>> foods = traverson
//                .follow("foods")
//                .toObject(new TypeReferences.ResourcesType<Resource<Food>>() {});

//        int totalItem = traverson.follow("foods", "find").
//                withTemplateParameters(parameters).
//                toObject("$.totalItems");
//        System.out.println(totalItem);
//        System.out.println(traverson.follow("foods").asLink());
//        return new ResponseEntity(foods.getContent(), HttpStatus.OK);
//        return new ResponseEntity(foods, HttpStatus.OK);
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