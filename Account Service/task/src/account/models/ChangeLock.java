package account.models;

import javax.validation.constraints.NotEmpty;

public class ChangeLock {
    @NotEmpty
    private String user;

    private String operation;

    public String getUser() {
        return user;
    }

    public String getOperation() {
        return operation;
    }

}
