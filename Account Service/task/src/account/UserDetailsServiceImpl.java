package account;

import account.comparators.SortByRole;
import account.models.User;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
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

}