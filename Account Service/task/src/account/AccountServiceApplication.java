package account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.AuthenticationException;

import java.util.Collections;
import java.util.Map;


@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher
            (ApplicationEventPublisher applicationEventPublisher) {
        Map<Class<? extends AuthenticationException>,
                        Class<? extends AbstractAuthenticationFailureEvent>> mapping =
                Collections.singletonMap(InternalAuthenticationServiceException.class, AuthenticationFailureBadCredentialsEvent.class);
        DefaultAuthenticationEventPublisher authenticationEventPublisher =
                new DefaultAuthenticationEventPublisher(applicationEventPublisher);
        authenticationEventPublisher.setAdditionalExceptionMappings(mapping);
        return authenticationEventPublisher;
    }
}