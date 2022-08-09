package account.models;

public enum RoleType {
    ROLE_ADMINISTRATOR("ADMINISTRATOR"),
    ROLE_USER("USER"),
    ROLE_ACCOUNTANT("ACCOUNTANT"),
    ROLE_AUDITOR("AUDITOR");

    private final String role;

    RoleType(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
