package projetPAQ.PAQBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projetPAQ.PAQBackend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    List<User> findByPlantAndRoleIn(String plant, List<String> roles);
   
    List<User> findByRole(String role);
    List<User> findByCreatedBy(Long createdBy);
    
    
}