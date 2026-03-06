package projetPAQ.PAQBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.PhaseDialoguePositif;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PhaseDialoguePositifRepository extends JpaRepository<PhaseDialoguePositif, Integer> {
    
    List<PhaseDialoguePositif> findByDeletedFalse();
    Optional<PhaseDialoguePositif> findByIdAndDeletedFalse(Integer id);
    List<PhaseDialoguePositif> findByCollaborateurIdAndDeletedFalse(Integer collaborateurId);
    List<PhaseDialoguePositif> findByDeletedTrue();
    List<PhaseDialoguePositif> findByUserIdAndDeletedFalse(Integer userId);
    List<PhaseDialoguePositif> findByUserIdAndDeletedTrue(Integer userId);
    List<PhaseDialoguePositif> findByCollaborateurAndDeletedFalseAndArchiveFalse(Collaborateur collaborateur);
    List<PhaseDialoguePositif> findByArchiveTrueAndDeletedFalse();
    
    List<PhaseDialoguePositif> findByDeletedTrueAndArchiveFalse();
    
    List<PhaseDialoguePositif> findByCollaborateurAndDeletedFalse(Collaborateur collaborateur);
    boolean existsByCollaborateurAndDeletedFalseAndDateBetween(Collaborateur collaborateur, Date start, Date end);

}