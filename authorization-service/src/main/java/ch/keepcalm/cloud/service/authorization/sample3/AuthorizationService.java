//package ch.keepcalm.cloud.service.authorization.sample3;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class AuthorizationService {
//
//    public static void main(String[] args) {
//        SpringApplication.run(AuthorizationService.class, args);
//    }
//}
//


//
//@Configuration
//@EnableResourceServer
//class ResourceServerConfig extends ResourceServerConfigurerAdapter {
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/").permitAll()
//                .antMatchers("/api/v1/**").authenticated();
//    }
//}
//
//
//@EnableAuthorizationServer
//class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Override
//    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//        security
//                .tokenKeyAccess("permitAll()")
//                .checkTokenAccess("isAuthenticated()")
//                .allowFormAuthenticationForClients();
//    }
//
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory().withClient("android-client")
//                .authorizedGrantTypes("client-credentials", "password","refresh_token")
//                .authorities("ROLE_CLIENT", "ROLE_ANDROID_CLIENT")
//                .scopes("read", "write", "trust")
//                .resourceIds("oauth2-resource")
//                .accessTokenValiditySeconds(5000)
//                .secret("android-secret").refreshTokenValiditySeconds(50000);
//    }
//
//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        endpoints.authenticationManager(authenticationManager)
//                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
//    }
//}
//
//@Service
//class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserService userService;
//
//    @Autowired
//    public CustomUserDetailsService(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        return this.userService.findByEmail(email);
//    }
//}
//
