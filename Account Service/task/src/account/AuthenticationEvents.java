package account;

import account.exceptions.BadRequestException;
import account.models.*;
import account.repositories.SecurityEventRepository;
import account.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class AuthenticationEvents {
    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SecurityEventRepository securityEventRepository;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent e) {
        UserDetailsImpl details = (UserDetailsImpl) e.getAuthentication().getPrincipal();

        User user = details.getUser();
        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user);
        }
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent e) {

        if(e.getClass().equals(AuthenticationFailureLockedEvent.class)) {
            return;
        }

        securityEventRepository.save(new SecurityEvent(
                new Date(),
                SecurityEventName.LOGIN_FAILED,
                e.getAuthentication().getName(),
                request.getRequestURI(),
                request.getRequestURI()
        ));

        UserDetailsImpl userDetails = userService.loadUserByUsername(e.getAuthentication().getPrincipal().toString());

        if (userDetails != null) {
            if (userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(RoleType.ROLE_ADMINISTRATOR.toString()))) {
                return;
            }

            if (userDetails.isEnabled() && userDetails.isAccountNonLocked()) {
                if (userDetails.getUser().getFailedAttempt() < UserDetailsServiceImpl.MAX_FAILED_ATTEMPTS - 1) {
                    userService.increaseFailedAttempts(userDetails.getUser());

                } else {
                    securityEventRepository.save(new SecurityEvent(
                            new Date(),
                            SecurityEventName.BRUTE_FORCE,
                            userDetails.getEmail(),
                            request.getRequestURI(),
                            request.getRequestURI()
                    ));

                    userService.lock(userDetails.getUser(), userDetails.getEmail(), request.getRequestURI());
                }
            }
        }
    }
}
