package account;

import account.exceptionHandlers.CustomAccessDeniedHandler;
import account.models.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

// Extending the adapter and adding the annotation
@Configuration
@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {
    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
            .and()
            .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
            .and()
            .authorizeRequests() // mana
            .mvcMatchers("/api/auth/signup").permitAll()
            .mvcMatchers(HttpMethod.GET, "/api/empl/payment")
                .hasAnyAuthority(RoleType.ROLE_ACCOUNTANT.toString(), RoleType.ROLE_USER.toString())
            .mvcMatchers(HttpMethod.POST, "/api/acct/payments")
                .hasAuthority(RoleType.ROLE_ACCOUNTANT.toString())
            .mvcMatchers("/api/admin/**").hasAuthority(RoleType.ROLE_ADMINISTRATOR.toString())
            .mvcMatchers("/api/security/**").hasAuthority(RoleType.ROLE_AUDITOR.toString())
            .mvcMatchers("/api/**").authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }


    @Bean("encoder")
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean("listOfBreachedPasswords")
    public List<String> getListOfBreachedPasswords() {
        return List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
    }
}