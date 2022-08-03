package account;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

@Entity
@IdClass(PayrollId.class)
@Table(uniqueConstraints={@UniqueConstraint(name = "unique_employee_period",  columnNames ={"employee","period"})})
public class Payroll implements Serializable {

    @Id
    @Column(name = "employee")
    @Email
    private String employee;

    @Id
    @Column(name = "period")
    @NotBlank
    @Pattern(regexp = "(0[1-9]|1[0-2])-\\d{4}")
    private String period;

    @Min(value = 0)
    @Column
    @NotNull
    private Long salary;

    public Payroll() {
    }

    public Payroll(String employee, String period, Long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}

