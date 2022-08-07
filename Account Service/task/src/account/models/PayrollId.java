package account.models;

import java.io.Serializable;
import java.util.Objects;

public class PayrollId implements Serializable {

    private String employee;
    private String period;

    public PayrollId() {
    }

    public PayrollId(String employee, String period) {
        this.employee = employee;
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayrollId accountId = (PayrollId) o;
        return employee.equals(accountId.employee) &&
                period.equals(accountId.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period);
    }
}
