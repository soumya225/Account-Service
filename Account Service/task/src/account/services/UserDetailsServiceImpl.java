package account.services;

import account.models.SecurityEvent;
import account.models.SecurityEventName;
import account.models.User;
import account.models.UserDetailsImpl;
import account.repositories.SecurityEventRepository;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    SecurityEventRepository securityEventRepo;

    public static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null) {
            return null;
        }

        Optional<User> optionalUser = userRepo.findUserByEmail(username.toLowerCase());

        if(optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        return new UserDetailsImpl(user);
    }

    public void deleteUserByUsername(String username) {
        Optional<User> user = userRepo.findUserByEmail(username.toLowerCase());

        user.ifPresent(val -> userRepo.delete(val));
    }

    public void save(User user) {
        userRepo.save(user);
    }

    public long getUserCount() {
        return userRepo.count();
    }

    public List<UserDetailsImpl> getUsers() {
        Iterable<User> users = userRepo.findAll();

        List<UserDetailsImpl> userDetailsList = new ArrayList<>();

        users.forEach(user -> {
            userDetailsList.add(new UserDetailsImpl(user));
        });

        return  userDetailsList;
    }

    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        userRepo.save(user);
    }

    public void resetFailedAttempts(User user) {
        user.setFailedAttempt(0);
        userRepo.save(user);
    }

    public void lock(User user, String subject, String path) {
        user.setAccountNonLocked(false);

        securityEventRepo.save(new SecurityEvent(
                new Date(),
                SecurityEventName.LOCK_USER,
                subject,
                "Lock user " + user.getEmail(),
                path
        ));

        userRepo.save(user);
    }

    public void unlock(User user, String subject, String path) {
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        securityEventRepo.save(new SecurityEvent(
                new Date(),
                SecurityEventName.UNLOCK_USER,
                subject,
                "Unlock user " + user.getEmail(),
                path
        ));

        userRepo.save(user);
    }

}