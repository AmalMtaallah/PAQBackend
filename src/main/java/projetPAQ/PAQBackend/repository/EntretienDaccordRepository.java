package projetPAQ.PAQBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienDaccord;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EntretienDaccordRepository extends JpaRepository<EntretienDaccord, Integer> {

    // Récupérer tous les entretiens non supprimés
    List<EntretienDaccord> findByDeletedFalse();

    // Récupérer un entretien non supprimé par ID
    Optional<EntretienDaccord> findByIdAndDeletedFalse(Integer id);

    // Récupérer les entretiens d'un collaborateur non supprimés
    List<EntretienDaccord> findByCollaborateurIdAndDeletedFalse(Integer collaborateurId);

    // Récupérer les entretiens supprimés (corbeille)
    List<EntretienDaccord> findByDeletedTrue();
    
    
    
    List<EntretienDaccord> findByUserIdAndDeletedFalse(Integer userId);
    List<EntretienDaccord> findByUserIdAndDeletedTrue(Integer userId);
    
    List<EntretienDaccord> findByCollaborateurAndDeletedFalseAndArchiveFalse(Collaborateur collaborateur);
    
    List<EntretienDaccord> findByArchiveTrueAndDeletedFalse();
    
    
    List<EntretienDaccord> findByDeletedTrueAndArchiveFalse();

    
    
    List<EntretienDaccord> findByCollaborateurAndDeletedFalse(Collaborateur collaborateur);
    boolean existsByCollaborateurAndDeletedFalseAndDateBetween(Collaborateur collaborateur, Date start, Date end);

	//List<Collaborateur> findArchivedAndNotDeletedEntretiens();
    
    
    @Query("SELECT COUNT(e) FROM EntretienDaccord e WHERE e.deleted = false AND e.date BETWEEN :startDate AND :endDate")
    Long countByDateBetweenAndDeletedFalse(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    @Query("SELECT COUNT(e) FROM EntretienDaccord e WHERE e.deleted = false AND e.archive = false")
    Long countByDeletedFalseAndArchivedFalse();
}