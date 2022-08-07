package account.models;

import java.util.List;

public class UserInfoReceipt {
    private final int id;
    private final String name;
    private final String lastname;
    private final String email;

    private final List<RoleType> roles;

    public UserInfoReceipt(int id,
                           String name,
                           String lastname,
                           String email,
                           List<RoleType> roles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
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

    public List<RoleType> getRoles() {
        return roles;
    }
}
