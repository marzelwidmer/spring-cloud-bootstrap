package ch.keepcalm.cloud.service.uaa

import com.nulabinc.zxcvbn.Zxcvbn
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.CrudRepository
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Repository
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.validation.Valid
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.swing.text.html.HTML.Tag.U

//https://www.codebyamir.com/blog/user-account-registration-with-spring-boot

@SpringBootApplication
@EnableWebSecurity
class UAAService() {
    @Bean
    fun getBCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

}

fun main(args: Array<String>) {
    runApplication<UAAService>(*args)
}

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/registration").setViewName("/registration")
        registry.addViewController("/confirm").setViewName("/confirm")
    }
}


@Entity
@Table(name = "user")
data class User(

        @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @Column(name = "id")
        val id: Long = 0,


        @Column(name = "email", nullable = false, unique = true)
        @Email(message = "Please provide a valid e-mail")
        @NotEmpty(message = "Please provide an e-mail")
        var email: String = "",

        @Column(name = "password")
        var password: String = "",

        @Column(name = "first_name")
        @NotEmpty(message = "Please provide your first name")
        var firstName: String = "",

        @Column(name = "last_name")
        @NotEmpty(message = "Please provide your last name")
        var lastName: String = "",

        @Column(name = "enabled")
        var enabled: Boolean = false,

        @Column(name = "confirmation_token")
        var confirmationToken: String = ""

)


@Repository("userRepository")
interface UserRepository : CrudRepository<User, Long> {

    fun findByEmail(email: String): Optional<User>
    fun findByConfirmationToken(confirmationToken: String): Optional<User>
}


@Service("userService")
class UserService(private val userRepository: UserRepository) {


    fun findByEmail(email: String): Optional<User> =
            userRepository.findByEmail(email)

    fun findByConfirmationToken(confirmationToken: String): Optional<User> =
            userRepository.findByConfirmationToken(confirmationToken)


    fun saveUser(user: User) =
            userRepository.save(user)


}


@Service("emailService")
class EmailService @Autowired
constructor(private val mailSender: JavaMailSender) {

    @Async
    fun sendEmail(email: SimpleMailMessage) {
        mailSender.send(email)
    }
}


@Controller
class RegisterController(
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val userService: UserService,
        private val emailService: EmailService
) {


    @GetMapping(value = ["/registration"])
    fun registrationPage(modelAndView: ModelAndView, user: User): ModelAndView {
        modelAndView.addObject("user", user)
        modelAndView.viewName = "registration"
        return modelAndView
    }

    //    // Process form input data
    @PostMapping(value = ["/registration"])
    fun registrationForm(modelAndView: ModelAndView, @Valid user: User, bindingResult: BindingResult, request: HttpServletRequest): ModelAndView {

        // Lookup user in database by e-mail
        val userExists = userService.findByEmail(user.email)


        if (userExists.isPresent) {
            modelAndView.addObject("alreadyRegisteredMessage", "Oops!  There is already a user registered with the email provided.")
            modelAndView.viewName = "registration"
            bindingResult.reject("email")
        }

        if (bindingResult.hasErrors()) {
            modelAndView.viewName = "registration"
        } else { // new user so we create user and send confirmation e-mail

            // Disable user until they click on confirmation link in email
            user.enabled = false

            // Generate random 36-character string token for confirmation link
            user.confirmationToken = UUID.randomUUID().toString()

            // Set new password
            user.password = bCryptPasswordEncoder.encode(user.password)

            // Save User in Database
            userService.saveUser(user)

            val appUrl = "${request.scheme}://${request.serverName}:${request.serverPort}"

            val registrationEmail = SimpleMailMessage()
            registrationEmail.setTo(user.email)
            registrationEmail.setSubject("Verify Your Account")
            registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
                    + appUrl + "/confirm?token=" + user.confirmationToken)
            registrationEmail.setFrom("noreply@domain.com")

            emailService.sendEmail(registrationEmail)

            modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + user.email)
            modelAndView.viewName = "registration"
        }

        return modelAndView

    }
}


@Controller
class ConfirmrController(
        private val userService: UserService
) {

    @GetMapping("/confirm")
    fun showConfirmationPage(@RequestParam(value = "token") token: String, modelAndView: ModelAndView, user: User): ModelAndView {


        modelAndView.viewName = "confirm"
        // Find the user associated with the reset token
        val user = userService.findByConfirmationToken(token)

        if (user.isPresent) { // Token found
            modelAndView.addObject("confirmationToken", user.get().confirmationToken)
            modelAndView.addObject("user", user.get())

            // Set user to enabled
            user.get().enabled = true

            // Save user
            userService.saveUser(user.get())

        } else {// No token found in DB
            modelAndView.addObject("invalidToken", "Oops!  This is an invalid confirmation link.")
        }
        return modelAndView
    }
}


@Configuration
@EnableWebSecurity
class WebSecurityConfig constructor(val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {

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
        //H2 database console runs inside a frame, So we need to disable X-Frame-Options
        http.headers().frameOptions().disable()
    }
}





//
//    // Process confirmation link
//    @PostMapping(value = ["/confirmToken"])
//    fun confirmationForm(modelAndView: ModelAndView, bindingResult: BindingResult, @RequestParam requestParams: Map<*, *>, redir: RedirectAttributes): ModelAndView {
//
//        modelAndView.viewName = "registration"
//
//        // Find the user associated with the reset token
//        val user = userService.findByConfirmationToken(requestParams["token"] as String).get()
//
//        // Set user to enabled
//        user.enabled = true
//
//        // Save user
//        userService.saveUser(user)
//
//        modelAndView.addObject("successMessage", "Your password has been set!")
//        return modelAndView
//    }
//
//
//    // Return registration form template
//    @RequestMapping(value = ["/register"], method = arrayOf(RequestMethod.GET))
//    fun showRegistrationPage(modelAndView: ModelAndView, user: User): ModelAndView {
//        modelAndView.addObject("user", user)
//        modelAndView.viewName = "register"
//        return modelAndView
//    }
//
//
//    // Process form input data
//    @RequestMapping(value = ["/register"], method = arrayOf(RequestMethod.POST))
//    fun processRegistrationForm(modelAndView: ModelAndView, @Valid user: User, bindingResult: BindingResult, request: HttpServletRequest): ModelAndView {
//
//        // Lookup user in database by e-mail
//        val userExists = userService.findByEmail(user.email)
//
//        System.out.println(userExists)
//
//
//        if (userExists.isPresent) {
//            modelAndView.addObject("alreadyRegisteredMessage", "Oops!  There is already a user registered with the email provided.")
//            modelAndView.viewName = "register"
//            bindingResult.reject("email")
//        }
//
//        if (bindingResult.hasErrors()) {
//            modelAndView.viewName = "register"
//        } else { // new user so we create user and send confirmation e-mail
//
//            // Disable user until they click on confirmation link in email
//            user.enabled = false
//
//            // Generate random 36-character string token for confirmation link
//            user.confirmationToken = UUID.randomUUID().toString()
//
//            userService.saveUser(user)
//
//            val appUrl = "${request.scheme}://${request.serverName}:${request.serverPort}"
//
//            val registrationEmail = SimpleMailMessage()
//            registrationEmail.setTo(user.email)
//            registrationEmail.setSubject("Registration Confirmation")
//            registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
//                    + appUrl + "/confirm?token=" + user.confirmationToken)
//            registrationEmail.setFrom("noreply@domain.com")
//
//            emailService.sendEmail(registrationEmail)
//
//            modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + user.email)
//            modelAndView.viewName = "register"
//        }
//
//        return modelAndView
//    }

// Process confirmation link
//    @RequestMapping(value = ["/confirm"], method = arrayOf(RequestMethod.GET))
//    fun showConfirmationPage(modelAndView: ModelAndView, @RequestParam("token") token: String): ModelAndView {
//
//        val user = userService.findByConfirmationToken(token)
//
//        if (user.isPresent) { // Token found
//            modelAndView.addObject("confirmationToken", user.get().confirmationToken)
//        } else {// No token found in DB
//            modelAndView.addObject("invalidToken", "Oops!  This is an invalid confirmation link.")
//        }
//
//        modelAndView.viewName = "confirm"
//        return modelAndView
//    }

// Process confirmation link
//    @RequestMapping(value = ["/confirm"], method = arrayOf(RequestMethod.POST))
//    fun processConfirmationForm(modelAndView: ModelAndView, bindingResult: BindingResult, @RequestParam requestParams: Map<*, *>, redir: RedirectAttributes): ModelAndView {
//
//        modelAndView.viewName = "confirm"
//
//        val passwordCheck = Zxcvbn()
//
//        val strength = passwordCheck.measure(requestParams["password"] as String?)
//
//        if (strength.getScore() < 3) {
//            bindingResult.reject("password")
//
//            redir.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.")
//
//            modelAndView.viewName = "redirect:confirm?token=" + requestParams["token"]
//            println(requestParams["token"])
//            return modelAndView
//        }
//
//        // Find the user associated with the reset token
//        val user = userService.findByConfirmationToken(requestParams["token"] as String).get()
//
//        // Set new password
//        user.password = bCryptPasswordEncoder.encode(requestParams["password"] as CharSequence?)
//
//        // Set user to enabled
//        user.enabled = true
//
//        // Save user
//        userService.saveUser(user)
//
//        modelAndView.addObject("successMessage", "Your password has been set!")
//        return modelAndView
//    }
//



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