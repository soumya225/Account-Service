package account.exceptionHandlers;

import account.models.SecurityEvent;
import account.models.SecurityEventName;
import account.repositories.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    SecurityEventRepository securityEventRepository;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException, ServletException {

        securityEventRepository.save(new SecurityEvent(
                new Date(),
                SecurityEventName.ACCESS_DENIED,
                request.getRemoteUser(),
                request.getRequestURI(),
                request.getRequestURI()
        ));

        response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");
    }
}
