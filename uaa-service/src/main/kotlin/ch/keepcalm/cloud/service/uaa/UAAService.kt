package ch.keepcalm.cloud.service.uaa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@SpringBootApplication
@EnableWebSecurity
class UAAService

fun main(args: Array<String>) {
    runApplication<UAAService>(*args)
}

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/sign-up").setViewName("sign-up")
        registry.addViewController("/sign-in").setViewName("sign-in")
        registry.addViewController("/login").setViewName("login")
        registry.addViewController("/user-info").setViewName("user-info")
//        registry.addViewController("/").setViewName("redirect:/message")
    }
}



@Configuration
@EnableWebSecurity
class WebSecurityConfig constructor(val userDetailsService : UserDetailsService) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }
//    override fun configure(http: HttpSecurity) {
//        http
//                .authorizeRequests()
//                .antMatchers("/js/**", "/css/**", "/img/**", "/webjars/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .permitAll()
//                .and()
//                .logout()
//                .permitAll()
//    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // https://stackoverflow.com/questions/48902706/spring-cloud-eureka-with-spring-security/
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()
    }
}



























//
//@EnableWebSecurity
//class SecurityConfig : WebSecurityConfigurerAdapter() {
//
//    @Autowired
//    private val accessDeniedHandler: LoggingAccessDeniedHandler? = null
//
//    @Throws(Exception::class)
//    override fun configure(http: HttpSecurity) {
//        http
//                .authorizeRequests()
//                .antMatchers(
//                        "/",
//                        "/js/**",
//                        "/css/**",
//                        "/img/**",
//                        "/webjars/**").permitAll()
//                .antMatchers("/user/**").hasRole("USER")
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .permitAll()
//                .and()
//                .logout()
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
//                .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/login?logout")
//                .permitAll()
//                .and()
//                .exceptionHandling()
//                .accessDeniedHandler(accessDeniedHandler)
//    }
//
//    @Throws(Exception::class)
//    override fun configure(auth: AuthenticationManagerBuilder?) {
//        auth!!.inMemoryAuthentication()
//                .withUser("user").password("password").roles("USER")
//                .and()
//                .withUser("manager").password("password").roles("MANAGER")
//    }
//
//}
//
//@Component
//class LoggingAccessDeniedHandler : AccessDeniedHandler {
//    companion object {
//        private val LOG = LoggerFactory.getLogger(LoggingAccessDeniedHandler::class.java)
//    }
//
//    @Throws(IOException::class, ServletException::class)
//    override fun handle(request: HttpServletRequest?, response: HttpServletResponse?, ex: org.springframework.security.access.AccessDeniedException?) {
//        val auth = SecurityContextHolder.getContext().authentication
//
//        if (auth != null) {
//            LOG.info(auth.name
//                    + " was trying to access protected resource: "
//                    + (request?.getRequestURI()))
//        }
//
//        response?.sendRedirect((request?.getContextPath()) + "/access-denied")
//    }
//}
//
//@Controller
//class HomeController {
//
//    @GetMapping("/")
//    fun root(): String {
//        return "index"
//    }
//
//    @GetMapping("/user")
//    fun userIndex(): String {
//        return "user/index"
//    }
//
//    @GetMapping("/login")
//    fun login(): String {
//        return "login"
//    }
//
//    @GetMapping("/access-denied")
//    fun accessDenied(): String {
//        return "/error/access-denied"
//    }
//
//}