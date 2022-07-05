package account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignUpController {

    @PostMapping("/api/auth/signup")
    public ResponseEntity<UserDetails> signUp(@RequestBody User user) {
        if(!StringUtils.isEmpty(user.getName()) &&
                !StringUtils.isEmpty(user.getLastname()) &&
                !StringUtils.isEmpty(user.getPassword()) &&
                user.getEmail().endsWith("@acme.com")) {
            return new ResponseEntity<>(new UserDetails(user.getName(),
                    user.getLastname(),
                    user.getEmail()),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
