package ch.keepcalm.cloud.service.nutrition

import ch.keepcalm.cloud.service.nutrition.food.FoodRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@SpringBootApplication
class NutritionService(val foodRepository: FoodRepository) {

    @Bean
    fun init() = CommandLineRunner {
        val count = foodRepository.count()
        println("found $count records in Food database")
    }


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
    runApplication<NutritionService>(*args)
}