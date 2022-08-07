package account.models;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class ChangeRole {

    @NotEmpty
    private String user;

    private String role;

    private String operation;

    public String getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    public String getOperation() {
        return operation;
    }
}
