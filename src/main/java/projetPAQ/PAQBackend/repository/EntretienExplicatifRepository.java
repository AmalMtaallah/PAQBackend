package projetPAQ.PAQBackend.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienExplicatif;

public interface EntretienExplicatifRepository extends JpaRepository<EntretienExplicatif, Integer> {
	 List<EntretienExplicatif> findByCollaborateurId(Integer collaborateurId);
	// Récupérer tous les entretiens non supprimés
	    List<EntretienExplicatif> findByDeletedFalse();

	    // Récupérer les entretiens par collaborateur non supprimés
	    List<EntretienExplicatif> findByCollaborateurIdAndDeletedFalse(Integer collaborateurId);
	   // Récupérer un entretien par son ID uniquement s'il n'est pas supprimé
	    Optional<EntretienExplicatif> findByIdAndDeletedFalse(Integer id);
	    
	    List<EntretienExplicatif> findByUserIdAndDeletedFalse(Integer userId);
	    List<EntretienExplicatif> findByUserIdAndDeletedTrue(Integer userId);
	    
	    List<EntretienExplicatif> findByCollaborateurAndArchiveFalse(Collaborateur collaborateur);
	    
	    // Nouvelle méthode pour récupérer les entretiens par collaborateur et non supprimés
	    List<EntretienExplicatif> findByCollaborateurAndDeletedFalse(Collaborateur collaborateur);
	    List<EntretienExplicatif> findByCollaborateurAndDeletedFalseAndDateAfter(
	            Collaborateur collaborateur, Date date);
	    
	    List<EntretienExplicatif> findByCollaborateurAndDeletedFalseAndArchiveFalse(Collaborateur collaborateur);
	    @Query("SELECT e FROM EntretienExplicatif e WHERE e.collaborateur.id = :collaborateurId AND e.deleted = false AND e.archive = false")
	    List<EntretienExplicatif> findByCollaborateurIdAndNotDeletedAndNotArchived(@Param("collaborateurId") Integer collaborateurId);
	    List<EntretienExplicatif> findByArchiveTrueAndDeletedFalse();

	    List<EntretienExplicatif> findByDeletedTrueAndArchiveFalse();
	    
	    
	  //  List<EntretienExplicatif> findByCollaborateurAndDeletedFalse(Collaborateur collaborateur);
	    boolean existsByCollaborateurAndDeletedFalseAndDateBetween(Collaborateur collaborateur, java.util.Date startDate, java.util.Date endDate);

	 // Dans EntretienExplicatifRepository
	    @Query("SELECT COUNT(e) FROM EntretienExplicatif e WHERE e.deleted = false AND e.date BETWEEN :startDate AND :endDate")
	    Long countByDateBetweenAndDeletedFalse(@Param("startDate") java.util.Date startDate, @Param("endDate") java.util.Date endDate);
	   
	    @Query("SELECT COUNT(e) FROM EntretienExplicatif e WHERE e.deleted = false AND e.archive = false")
	    Long countByDeletedFalseAndArchivedFalse();

}
