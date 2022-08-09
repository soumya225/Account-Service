package account.repositories;

import account.models.SecurityEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityEventRepository extends CrudRepository<SecurityEvent, Long> {

}
