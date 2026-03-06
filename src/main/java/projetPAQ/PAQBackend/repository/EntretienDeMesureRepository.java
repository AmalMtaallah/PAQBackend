package projetPAQ.PAQBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienDeMesure;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EntretienDeMesureRepository extends JpaRepository<EntretienDeMesure, Integer> {

    // Récupérer tous les entretiens non supprimés
    List<EntretienDeMesure> findByDeletedFalse();

    // Récupérer un entretien non supprimé par ID
    Optional<EntretienDeMesure> findByIdAndDeletedFalse(Integer id);

    // Récupérer les entretiens d'un collaborateur non supprimés
    List<EntretienDeMesure> findByCollaborateurIdAndDeletedFalse(Integer collaborateurId);

    // Récupérer les entretiens supprimés (corbeille)
    List<EntretienDeMesure> findByDeletedTrue();

    // Récupérer les entretiens par userId (non supprimés)
    List<EntretienDeMesure> findByUserIdAndDeletedFalse(Integer userId);

    // Récupérer les entretiens supprimés par userId
    List<EntretienDeMesure> findByUserIdAndDeletedTrue(Integer userId);
    
    List<EntretienDeMesure> findByCollaborateurAndDeletedFalseAndArchiveFalse(Collaborateur collaborateur);
    
    // Nouvelle méthode pour récupérer un entretien par collaborateurId, non supprimé et non archivé
    Optional<EntretienDeMesure> findFirstByCollaborateurIdAndDeletedFalseAndArchiveFalse(Integer collaborateurId);
    @Query("SELECT e FROM EntretienDeMesure e WHERE e.deleted = false AND e.archive = true")
    List<EntretienDeMesure> findArchivedAndNotDeletedEntretiens();
    
    
    
    
    List<EntretienDeMesure> findByDeletedTrueAndArchiveFalse();
    
    
    List<EntretienDeMesure> findByCollaborateurAndDeletedFalse(Collaborateur collaborateur);
    boolean existsByCollaborateurAndDeletedFalseAndDateBetween(Collaborateur collaborateur, Date start, Date end);

    
    @Query("SELECT COUNT(e) FROM EntretienDeMesure e WHERE e.deleted = false AND e.date BETWEEN :startDate AND :endDate")
    Long countByDateBetweenAndDeletedFalse(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    @Query("SELECT COUNT(e) FROM EntretienDeMesure e WHERE e.deleted = false AND e.archive = false")
    Long countByDeletedFalseAndArchivedFalse();
}


