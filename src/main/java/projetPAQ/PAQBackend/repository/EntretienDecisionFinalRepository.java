package projetPAQ.PAQBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienDecisionFinal;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EntretienDecisionFinalRepository extends JpaRepository<EntretienDecisionFinal, Integer> {

    // Trouver tous les entretiens non supprimés
    List<EntretienDecisionFinal> findByDeletedFalse();
    
    // Trouver un entretien par ID non supprimé
    Optional<EntretienDecisionFinal> findByIdAndDeletedFalse(Integer id);
    
    // Trouver les entretiens par collaborateur
    //List<EntretienDecisionFinal> findByCollaborateurIdAndDeletedFalse(Integer collaborateurId);
    
    @Query("SELECT e FROM EntretienDecisionFinal e WHERE e.collaborateur.id = :collaborateurId AND e.deleted = false AND e.archive = false")
    List<EntretienDecisionFinal> findByCollaborateurIdAndNotDeletedAndNotArchived(@Param("collaborateurId") Integer collaborateurId);
    
    // Trouver les entretiens par créateur (user)
    List<EntretienDecisionFinal> findByUserIdAndDeletedFalse(Long userId);
    
    // Trouver les entretiens validés
    List<EntretienDecisionFinal> findByValidationCompleteTrueAndDeletedFalse();
    
    // Trouver les entretiens en attente de validation
    List<EntretienDecisionFinal> findByValidationCompleteFalseAndDeletedFalse();
    
    // Trouver les entretiens par groupe RH
    @Query("SELECT e FROM EntretienDecisionFinal e WHERE e.groupeRH.id = :groupeRHId AND e.deleted = false")
    List<EntretienDecisionFinal> findByGroupeRHId(@Param("groupeRHId") Long groupeRHId);
    
    // Trouver les entretiens validés par un RH spécifique
    @Query("SELECT e FROM EntretienDecisionFinal e WHERE e.validatedRH.id = :userId AND e.deleted = false")
    List<EntretienDecisionFinal> findByValidatedRH(@Param("userId") Long userId);

    @Query("SELECT e FROM EntretienDecisionFinal e WHERE e.id = :id AND e.deleted = false")
    Optional<EntretienDecisionFinal> findByIdAndNotDeleted(@Param("id") Integer id);
  
    // Pour toutes les requêtes de base
    @Override
    @Query("SELECT e FROM EntretienDecisionFinal e WHERE e.deleted = false")
    List<EntretienDecisionFinal> findAll();
    
    
    List<EntretienDecisionFinal> findByCollaborateurAndDeletedFalseAndArchiveFalse(Collaborateur collaborateur);
    @Query("SELECT e FROM EntretienDecisionFinal e WHERE e.deleted = false AND e.archive = true")
    List<EntretienDecisionFinal> findArchivedAndNotDeletedEntretiens();

    List<EntretienDecisionFinal> findByDeletedTrueAndArchiveFalse();
    
    List<EntretienDecisionFinal> findByCollaborateurAndDeletedFalse(Collaborateur collaborateur);
    boolean existsByCollaborateurAndDeletedFalseAndDateBetween(Collaborateur collaborateur, Date start, Date end);
    
    
    @Query("SELECT COUNT(e) FROM EntretienDecisionFinal e WHERE e.deleted = false AND e.date BETWEEN :startDate AND :endDate")
    Long countByDateBetweenAndDeletedFalse(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    @Query("SELECT COUNT(e) FROM EntretienDecisionFinal e WHERE e.deleted = false AND e.archive = false")
    Long countByDeletedFalseAndArchivedFalse();


}