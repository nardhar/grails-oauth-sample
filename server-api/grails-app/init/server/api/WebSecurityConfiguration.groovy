package server.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserDetailsService userDetailsService
    
    @Autowired
    CustomAuthenticationProvider authenticationProvider

    @Autowired
    UsernameStoringUrlAuthenticationFailureHandler authenticationFailureHandler

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder encoder = passwordEncoder()
        // modificar el authentication provider para que haga referencia a un CustomAuthenticationProvider
        // que combine ldap y la autenticacion por db
        /* auth.userDetailsService(userDetailsService)
            .passwordEncoder(encoder) */
        // probando el CustomAuthenticationProvider
        auth.authenticationProvider(authenticationProvider)
            .userDetailsService(userDetailsService)
            .passwordEncoder(encoder)
    }

    @Override
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage('/login')
                .permitAll()
                //.failureHandler(authenticationFailureHandler)
                //.failureForwardUrl('/login?error')
            .and()
                .authorizeRequests()
                .antMatchers("/assets/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .rememberMe()
                .rememberMeCookieName('myserver-rememberme')
                .rememberMeParameter('remember-me') // the checkbox should not have a value attr
                .tokenValiditySeconds(864000) // 10 days
                .userDetailsService(userDetailsService)
            .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        super.authenticationManagerBean()
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    @Bean
    UsernameStoringUrlAuthenticationFailureHandler authenticationFailureHandler() {
        return new UsernameStoringUrlAuthenticationFailureHandler()
    }

}
