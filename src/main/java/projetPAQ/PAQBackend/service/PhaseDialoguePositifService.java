package projetPAQ.PAQBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projetPAQ.PAQBackend.DTO.PhaseDialoguePositifDTO;
import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.PhaseDialoguePositif;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.CollaborateurRepository;
import projetPAQ.PAQBackend.repository.PhaseDialoguePositifRepository;
import projetPAQ.PAQBackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PhaseDialoguePositifService {

	 private final PhaseDialoguePositifRepository phaseDialoguePositifRepository;
	    private final CollaborateurRepository collaborateurRepository;
	    private final UserRepository userRepository;

	    @Autowired
	    public PhaseDialoguePositifService(PhaseDialoguePositifRepository phaseDialoguePositifRepository,
	                                     CollaborateurRepository collaborateurRepository,
	                                     UserRepository userRepository) {
	        this.phaseDialoguePositifRepository = phaseDialoguePositifRepository;
	        this.collaborateurRepository = collaborateurRepository;
	        this.userRepository = userRepository;
	    }

    public List<PhaseDialoguePositif> getAllPhases() {
        return phaseDialoguePositifRepository.findByDeletedFalse();
    }

    public Optional<PhaseDialoguePositif> getPhaseById(Integer id) {
        return phaseDialoguePositifRepository.findByIdAndDeletedFalse(id);
    }

    public PhaseDialoguePositif createPhase(PhaseDialoguePositif phase, String userRole) {
        if (!"SL".equals(userRole) && !"SGL".equals(userRole) && !"QMSegment".equals(userRole)) {
            throw new RuntimeException("Seuls les utilisateurs avec le rôle SL, SGL ou QMSegment peuvent ajouter une phase de dialogue positif.");
        }
        
        // Vérifier que le collaborateur est bien défini
        if (phase.getCollaborateur() == null) {
            throw new RuntimeException("Un collaborateur doit être associé à la phase.");
        }
        
        phase.setDeleted(false);
        return phaseDialoguePositifRepository.save(phase);
    }

    public PhaseDialoguePositif updatePhase(Integer id, PhaseDialoguePositif updatedPhase, String userRole) {
        if (!"SL".equals(userRole) && !"SGL".equals(userRole) && !"QMSegment".equals(userRole)) {
            throw new RuntimeException("Vous n'avez pas les droits pour modifier cette phase.");
        }

        Optional<PhaseDialoguePositif> existingPhaseOpt = findById(id);
        if (existingPhaseOpt.isEmpty()) {
            throw new RuntimeException("Phase non trouvée.");
        }

        PhaseDialoguePositif existingPhase = existingPhaseOpt.get();

        if (updatedPhase.getDate() != null) {
            existingPhase.setDate(updatedPhase.getDate());
        }
        if (updatedPhase.getCommentaires() != null) {
            existingPhase.setCommentaires(updatedPhase.getCommentaires());
        }

        return save(existingPhase);
    }

    public void deletePhase(Integer id, String userRole) {
        if (!"SL".equals(userRole)) {
            throw new RuntimeException("Seuls les utilisateurs avec le rôle SL peuvent supprimer une phase de dialogue positif.");
        }

        Optional<PhaseDialoguePositif> phaseOpt = phaseDialoguePositifRepository.findById(id);
        if (phaseOpt.isPresent()) {
            PhaseDialoguePositif phase = phaseOpt.get();
            phase.setDeleted(true);
            phaseDialoguePositifRepository.save(phase);
        } else {
            throw new RuntimeException("Phase non trouvée avec l'ID : " + id);
        }
    }

    public List<PhaseDialoguePositif> getDeletedPhases() {
        return phaseDialoguePositifRepository.findByDeletedTrueAndArchiveFalse();
    }

    public List<PhaseDialoguePositif> getPhasesByCollaborateurId(Integer collaborateurId) {
        return phaseDialoguePositifRepository.findByCollaborateurIdAndDeletedFalse(collaborateurId);
    }

    public PhaseDialoguePositif convertToEntity(PhaseDialoguePositifDTO dto) {
        PhaseDialoguePositif phase = new PhaseDialoguePositif();
        phase.setDate(dto.getDate());
        phase.setCommentaires(dto.getCommentaires());
        
        // Récupérer le collaborateur depuis la base de données
        Collaborateur collaborateur = collaborateurRepository.findById(dto.getCollaborateurId())
            .orElseThrow(() -> new RuntimeException("Collaborateur non trouvé"));
        phase.setCollaborateur(collaborateur);
        
        // Récupérer l'utilisateur depuis la base de données
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        phase.setUser(user);
        
        return phase;
    }
    public PhaseDialoguePositifDTO convertToDTO(PhaseDialoguePositif phase) {
        PhaseDialoguePositifDTO dto = new PhaseDialoguePositifDTO();
        dto.setId(phase.getId());
        dto.setDate(phase.getDate());
        dto.setCommentaires(phase.getCommentaires());
        
        // Informations du collaborateur
        if (phase.getCollaborateur() != null) {
            dto.setCollaborateurId(phase.getCollaborateur().getId());
            dto.setCollaborateurNom(phase.getCollaborateur().getNom());
            dto.setCollaborateurPrenom(phase.getCollaborateur().getPrenom());
        }
        
        // Informations de l'utilisateur (superviseur)
        if (phase.getUser() != null) {
            dto.setUserId(phase.getUser().getId());
            dto.setUserNom(phase.getUser().getFirstName());  // Supposant que User a getNom()
            dto.setUserPrenom(phase.getUser().getLastName());  // Supposant que User a getPrenom()
        }
        dto.setDateArchivage(phase.getDateArchivage()); 
        return dto;
    }

    public Optional<PhaseDialoguePositif> findById(Integer id) {
        return phaseDialoguePositifRepository.findById(id);
    }

    public PhaseDialoguePositif save(PhaseDialoguePositif phase) {
        return phaseDialoguePositifRepository.save(phase);
    }

    public Optional<PhaseDialoguePositif> getPhaseDetailsById(Integer id) {
        return phaseDialoguePositifRepository.findByIdAndDeletedFalse(id);
    }

    public List<PhaseDialoguePositif> getPhasesByUserId(Integer userId) {
        return phaseDialoguePositifRepository.findByUserIdAndDeletedFalse(userId);
    }

    public List<PhaseDialoguePositif> getDeletedPhasesByUserId(Integer userId) {
        return phaseDialoguePositifRepository.findByUserIdAndDeletedTrue(userId);
    }
    
    
    public List<PhaseDialoguePositif> getArchivedPhases() {
       // return phaseDialoguePositifRepository.findByArchiveTrue();
        // Ou si vous voulez seulement les archives non supprimées :
         return phaseDialoguePositifRepository.findByArchiveTrueAndDeletedFalse();
    }
    
    
    
}


