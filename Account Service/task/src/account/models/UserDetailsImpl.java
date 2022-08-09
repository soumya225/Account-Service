package account.models;

import account.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private final User user;
    private final int id;
    private final String name;
    private final String lastname;
    private final String email;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> rolesAndAuthorities;


    public UserDetailsImpl(User user) {
        this.user = user;
        id = user.getId();
        name = user.getName();
        lastname = user.getLastname();
        email = user.getEmail();
        username = user.getEmail();
        password = user.getPassword();

        rolesAndAuthorities = new ArrayList<>();

        user.getRoles().forEach(role -> rolesAndAuthorities.add(new SimpleGrantedAuthority(role.getRoleType().toString())));

    }

    public User getUser() {
        return user;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    // 4 remaining methods that just return true
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}