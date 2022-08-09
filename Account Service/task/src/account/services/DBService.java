package account.services;

import account.models.Role;
import account.models.RoleType;
import account.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Service
public class DBService {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    public void saveUser(User user, RoleType roleType) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Role role = new Role();
        role.setRoleType(roleType);
        role.setUser(user);

        user.addRole(role);

        entityManager.persist(user);
        entityManager.getTransaction().commit();

    }

    public void addRoleToUser(User user, RoleType roleType) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Role role = new Role();
        role.setRoleType(roleType);
        role.setUser(user);

        user.addRole(role);

        entityManager.merge(user);
        entityManager.getTransaction().commit();
    }

    public void removeRoleFromUser(User user, RoleType roleType) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Role role = null;
        for(Role existingRole: user.getRoles()) {
            if(existingRole.getRoleType().equals(roleType)) {
                role = existingRole;
            }
        }

        user.removeRole(role);

        entityManager.merge(user);
        entityManager.getTransaction().commit();
    }
}