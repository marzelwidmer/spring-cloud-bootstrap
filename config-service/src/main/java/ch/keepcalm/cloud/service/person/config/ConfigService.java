package ch.keepcalm.cloud.service.person.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableConfigServer
public class ConfigService {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ConfigService.class).run(args);
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
