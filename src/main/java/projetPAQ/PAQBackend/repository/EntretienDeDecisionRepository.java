package projetPAQ.PAQBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienDeDecision;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EntretienDeDecisionRepository extends JpaRepository<EntretienDeDecision, Integer> {
    List<EntretienDeDecision> findByDeletedFalse();
    Optional<EntretienDeDecision> findByIdAndDeletedFalse(Integer id);
    List<EntretienDeDecision> findByCollaborateurIdAndDeletedFalse(Integer collaborateurId);
    List<EntretienDeDecision> findByUserIdAndDeletedFalse(Long userId);
    List<EntretienDeDecision> findByValidatedHPIsNotNull();
    
    List<EntretienDeDecision> findByCollaborateurAndDeletedFalseAndArchiveFalse(Collaborateur collaborateur);
    @Query("SELECT e FROM EntretienDeDecision e WHERE e.deleted = false AND e.archive = true")
    List<EntretienDeDecision> findArchivedAndNotDeletedEntretiens();
    
    
    List<EntretienDeDecision> findByDeletedTrueAndArchiveFalse();

    List<EntretienDeDecision> findByCollaborateurAndDeletedFalse(Collaborateur collaborateur);
    boolean existsByCollaborateurAndDeletedFalseAndDateBetween(Collaborateur collaborateur, Date start, Date end);

    @Query("SELECT COUNT(e) FROM EntretienDeDecision e WHERE e.deleted = false AND e.date BETWEEN :startDate AND :endDate")
    Long countByDateBetweenAndDeletedFalse(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    @Query("SELECT COUNT(e) FROM EntretienDeDecision e WHERE e.deleted = false AND e.archive = false")
    Long countByDeletedFalseAndArchivedFalse();

}