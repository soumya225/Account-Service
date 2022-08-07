package account.comparators;

import account.models.Role;

import java.util.Comparator;

public class SortByRole implements Comparator<Role> {

    @Override
    public int compare(Role a, Role b) {
        return a.getRoleType().getRole().compareTo(b.getRoleType().getRole());
    }
}
