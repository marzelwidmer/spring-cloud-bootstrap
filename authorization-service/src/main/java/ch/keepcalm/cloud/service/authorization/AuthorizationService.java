package ch.keepcalm.cloud.service.authorization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
public class AuthorizationService {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationService.class, args);
    }
}


//https://docs.spring.io/spring-security-oauth2-boot/docs/current-SNAPSHOT/reference/htmlsingle/
@Component
@EnableAuthorizationServer
class CustomAuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    AuthenticationManager authenticationManager;

    public CustomAuthorizationServerConfigurer(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        this.authenticationManager =
                authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void configure(
            ClientDetailsServiceConfigurer clients
    ) throws Exception {
        clients.inMemory()
                .withClient("clientId")
                .authorizedGrantTypes("password")
                .secret("{noop}secret")
                .scopes("all", "read", "write");
    }

    @Override
    public void configure(
            AuthorizationServerEndpointsConfigurer endpoints
    ) throws Exception {
        endpoints.authenticationManager(authenticationManager);
    }
}


@Configuration
@EnableResourceServer
class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/me")
                .authorizeRequests().anyRequest().authenticated();
    }
}

@Slf4j
@RestController
class AuthFooBarController {


    @GetMapping({"/me"})
    public Map<String, String> user(Principal principal) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", principal.getName());
        return map;
    }

    @GetMapping({"/foobar"})
    public String fooBar(Principal principal) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", principal.getName());
        return "Hello...";
    }

}
