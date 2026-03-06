package projetPAQ.PAQBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.User;

import java.util.List;
import java.util.Optional;

public interface CollaborateurRepository extends JpaRepository<Collaborateur, Integer> {
	//List<Collaborateur> findByUserId(Long long1);
	
	@Query("SELECT c FROM Collaborateur c LEFT JOIN FETCH c.user WHERE c.deleted = false")
	List<Collaborateur> findAllWithUsers();;
	 
	 List<Collaborateur> findByUserId(Long userId);
	 
	 
	 // Méthode pour rechercher des collaborateurs par nom et prénom
	  /*  @Query("SELECT c FROM Collaborateur c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')) " +
	           "AND LOWER(c.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))")
	    List<Collaborateur> findByNomAndPrenom(@Param("nom") String nom, @Param("prenom") String prenom);
	    */
	    
	    
	    
	   // Récupérer les collaborateurs non supprimés
	    List<Collaborateur> findByDeletedFalse();

	    // Récupérer les collaborateurs supprimés (corbeille)
	    List<Collaborateur> findByDeletedTrue();

	    // Marquer un collaborateur comme supprimé
	    @Modifying
	    @Query("UPDATE Collaborateur c SET c.deleted = true WHERE c.id = :id")
	    void softDeleteById(@Param("id") Integer id);

	    // Rechercher des collaborateurs par nom et prénom (non supprimés)
	    @Query("SELECT c FROM Collaborateur c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')) " +
	           "AND LOWER(c.prenom) LIKE LOWER(CONCAT('%', :prenom, '%')) AND c.deleted = false")
	    List<Collaborateur> findByNomAndPrenom(@Param("nom") String nom, @Param("prenom") String prenom);

	    // Récupérer les collaborateurs par ID d'utilisateur (non supprimés)
	    List<Collaborateur> findByUserIdAndDeletedFalse(Long userId);
	 
	    
	    boolean existsByIdAndDeletedFalse(Integer id);   
	    
	    
	    boolean existsById(Integer id);
	    
	    @Query("SELECT c.user FROM Collaborateur c WHERE c.id = :collaborateurId")
	    Optional<User> findUserCreatorByCollaborateurId(@Param("collaborateurId") Integer collaborateurId); 

	    @Modifying
	    @Query("UPDATE Collaborateur c SET c.user = :newUser WHERE c.id = :collaborateurId")
	    void updateCollaborateurUserId(@Param("collaborateurId") Integer collaborateurId,
	                                   @Param("newUser") User newUser);
	    
	    
	    
	    // Méthode pour récupérer les collaborateurs supprimés par userId
	    List<Collaborateur> findByUserIdAndDeletedTrue(Long userId);

	    // Méthode pour récupérer les collaborateurs supprimés par plant
	    @Query("SELECT c FROM Collaborateur c JOIN c.user u WHERE c.deleted = true AND u.plant = :plant")
	    List<Collaborateur> findByPlantAndDeletedTrue(@Param("plant") String plant);
	    @Query("SELECT COUNT(c) FROM Collaborateur c WHERE c.deleted = false")
	    Long countByDeletedFalse();
}