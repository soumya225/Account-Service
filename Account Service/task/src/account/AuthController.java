package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    List<String> listOfBreachedPasswords;

    @PostMapping("/signup")
    public ResponseEntity<UserInfoReceipt> signUp(@Valid @RequestBody User user) {
        user.setEmail(user.getEmail().toLowerCase());

        if(user.getEmail().endsWith("@acme.com")) {
            UserDetails existingUser = userDetailsServiceImpl.loadUserByUsername(user.getEmail());
            if(existingUser == null) {

                if(listOfBreachedPasswords.contains(user.getPassword())) {
                    throw new BadRequestException("The password is in the hacker's database!");
                }

                user.setPassword(encoder.encode(user.getPassword()));

                userDetailsServiceImpl.save(user);

                return new ResponseEntity<>(new UserInfoReceipt(user.getId(), user.getName(), user.getLastname(), user.getEmail()), HttpStatus.OK);
            } else {
                throw new BadRequestException("User exist!");
            }
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/changepass")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePassword changePassword, @AuthenticationPrincipal UserDetailsImpl details) {

        if(listOfBreachedPasswords.contains(changePassword.getNew_password())) {
            throw new BadRequestException("The password is in the hacker's database!");
        }

        if(encoder.matches(changePassword.getNew_password(), details.getPassword())) {
            throw new BadRequestException("The passwords must be different!");
        }

        details.getUser().setPassword(encoder.encode(changePassword.getNew_password()));

        userDetailsServiceImpl.save(details.getUser());

        Map<String, String> map = new HashMap<>();
        map.put("email", details.getEmail());
        map.put("status", "The password has been updated successfully");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
