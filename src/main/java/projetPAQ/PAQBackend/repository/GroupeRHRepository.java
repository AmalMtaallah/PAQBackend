package projetPAQ.PAQBackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import projetPAQ.PAQBackend.entity.GroupeRH;
import projetPAQ.PAQBackend.entity.User;

public interface GroupeRHRepository extends JpaRepository<GroupeRH, Long> {
    // Méthodes personnalisées si nécessaire
	@Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);
	
	List<GroupeRH> findByPlant(String plant);
}
