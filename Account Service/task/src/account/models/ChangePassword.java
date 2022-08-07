package account.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePassword {

    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @NotBlank
    private String new_password;

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
