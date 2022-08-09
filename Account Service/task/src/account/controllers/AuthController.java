package account.controllers;

import account.exceptions.BadRequestException;
import account.models.*;
import account.services.DBService;
import account.repositories.SecurityEventRepository;
import account.services.UserDetailsServiceImpl;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    SecurityEventRepository securityEventRepository;

    @Autowired
    DBService dbService;

    @Autowired
    List<String> listOfBreachedPasswords;

    @PostMapping("/signup")
    public ResponseEntity<UserInfoReceipt> signUp(@Valid @RequestBody User user, HttpServletRequest request) {
        user.setEmail(user.getEmail().toLowerCase());

        if(user.getEmail().endsWith("@acme.com")) {
            UserDetails existingUser = userDetailsServiceImpl.loadUserByUsername(user.getEmail());
            if(existingUser == null) {

                if(listOfBreachedPasswords.contains(user.getPassword())) {
                    throw new BadRequestException("The password is in the hacker's database!");
                }

                user.setPassword(encoder.encode(user.getPassword()));
                user.setEmail(user.getEmail().toLowerCase());

                if (userDetailsServiceImpl.getUserCount() == 0) {
                    dbService.saveUser(user, RoleType.ROLE_ADMINISTRATOR);

                } else {
                    dbService.saveUser(user, RoleType.ROLE_USER);
                }

                List<RoleType> roleTypes = new ArrayList<>();
                user.getRoles().forEach(role -> roleTypes.add(role.getRoleType()));

                securityEventRepository.save(new SecurityEvent(
                    new Date(),
                    SecurityEventName.CREATE_USER,
                    "Anonymous",
                    user.getEmail(),
                    request.getRequestURI()
                ));

                return new ResponseEntity<>(
                        new UserInfoReceipt(user.getId(),
                                            user.getName(),
                                            user.getLastname(),
                                            user.getEmail(),
                                            roleTypes),
                        HttpStatus.OK);
            } else {
                throw new BadRequestException("User exist!");
            }
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/changepass")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePassword changePassword,
                                                              @AuthenticationPrincipal UserDetailsImpl details,
                                                              HttpServletRequest request) {

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

        securityEventRepository.save(new SecurityEvent(
                new Date(),
                SecurityEventName.CHANGE_PASSWORD,
                details.getEmail(),
                details.getEmail(),
                request.getRequestURI()
        ));

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
