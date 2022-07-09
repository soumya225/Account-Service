package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SignUpController {
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<UserInfoReceipt> signUp(@RequestBody User user) {
        user.setEmail(user.getEmail().toLowerCase());

        if(!StringUtils.isEmpty(user.getName()) &&
                !StringUtils.isEmpty(user.getLastname()) &&
                !StringUtils.isEmpty(user.getPassword()) &&
                user.getEmail().endsWith("@acme.com")) {

            System.out.println(user);

            UserDetails existingUser = userDetailsServiceImpl.loadUserByUsername(user.getEmail());

            System.out.println(existingUser);
            if(existingUser == null) {

                user.setPassword(encoder.encode(user.getPassword()));

                userDetailsServiceImpl.save(user);

                return new ResponseEntity<>(new UserInfoReceipt(user.getId(), user.getName(), user.getLastname(), user.getEmail()), HttpStatus.OK);
            } else {
                throw new UserExistsException("User exist!");
            }
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
