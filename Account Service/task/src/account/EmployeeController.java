package account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

    @GetMapping("/api/empl/payment")
    public ResponseEntity<UserInfoReceipt> checkPayment(@AuthenticationPrincipal UserDetailsImpl details) {
        return new ResponseEntity<>(new UserInfoReceipt(details.getId(), details.getName(), details.getLastname(), details.getEmail().toLowerCase()), HttpStatus.OK);
    }
}
