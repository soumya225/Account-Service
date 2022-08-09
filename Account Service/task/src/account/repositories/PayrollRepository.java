package account.repositories;

import account.models.Payroll;
import account.models.PayrollId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends CrudRepository<Payroll, PayrollId> {

    List<Optional<Payroll>> findByEmployee(String employee);
}
