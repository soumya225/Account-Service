package account.repositories;

import account.models.Payroll;
import account.models.PayrollId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends CrudRepository<Payroll, PayrollId> {

    List<Optional<Payroll>> findByEmployee(String employee);
}