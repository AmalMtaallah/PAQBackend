package projetPAQ.PAQBackend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import projetPAQ.PAQBackend.DTO.EntretienDeDecisionDTO;
import projetPAQ.PAQBackend.entity.EntretienDeDecision;
import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.EntretienDeDecisionRepository;
import projetPAQ.PAQBackend.repository.CollaborateurRepository;
import projetPAQ.PAQBackend.repository.UserRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntretienDeDecisionService {

    private final EntretienDeDecisionRepository entretienRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final UserRepository userRepository;

    @Autowired
    public EntretienDeDecisionService(
            EntretienDeDecisionRepository entretienRepository,
            CollaborateurRepository collaborateurRepository,
            UserRepository userRepository) {
        this.entretienRepository = entretienRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.userRepository = userRepository;
    }

    public EntretienDeDecision createEntretien(EntretienDeDecision entretien, Integer collaborateurId, Long userId) {
        Collaborateur collaborateur = collaborateurRepository.findById(collaborateurId)
                .orElseThrow(() -> new RuntimeException("Collaborateur non trouvé"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        entretien.setCollaborateur(collaborateur);
        entretien.setUser(user);
        entretien.setDeleted(false);

        return entretienRepository.save(entretien);
    }

    public EntretienDeDecision validateByHP(Integer id, String email) {
        EntretienDeDecision entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));

        User validatedHP = userRepository.findByEmail(email);
        if (validatedHP == null) {
            throw new RuntimeException("Utilisateur HP non trouvé avec l'email: " + email);
        }

        entretien.setValidatedHP(validatedHP);
        entretien.setHPValidationDate(new Date(System.currentTimeMillis()));
        
        return entretienRepository.save(entretien);
    }
    public EntretienDeDecision validateByQM(Integer id, String email) {
        EntretienDeDecision entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));

        User validatedQM = userRepository.findByEmail(email);
        if (validatedQM == null) {
            throw new RuntimeException("Utilisateur QM non trouvé avec l'email: " + email);
        }

        entretien.setValidatedQM(validatedQM);
        entretien.setQMValidationDate(new Date(System.currentTimeMillis()));
        
        return entretienRepository.save(entretien);
    }


    public List<EntretienDeDecision> getAllEntretiens() {
        return entretienRepository.findByDeletedFalse();
    }

    public Optional<EntretienDeDecision> getEntretienById(Integer id) {
        return entretienRepository.findByIdAndDeletedFalse(id);
    }

    public EntretienDeDecision updateEntretien(Integer id, EntretienDeDecisionDTO entretienDTO) {
        EntretienDeDecision existingEntretien = entretienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));

        if (entretienDTO.getDate() != null) {
            existingEntretien.setDate(entretienDTO.getDate());
        }
        if (entretienDTO.getTypeErreur() != null) {
            existingEntretien.setTypeErreur(entretienDTO.getTypeErreur());
        }
        if (entretienDTO.getDetails() != null) {
            existingEntretien.setDetails(entretienDTO.getDetails());
        }
        if (entretienDTO.getDecision() != null) {
            existingEntretien.setDecision(entretienDTO.getDecision());
        }
        // Reset validation fields to null
        existingEntretien.setValidatedHP(null);
        existingEntretien.setValidatedQM(null);
        existingEntretien.setHPValidationDate(null);
        existingEntretien.setQMValidationDate(null);

        return entretienRepository.save(existingEntretien);
    }

    public void deleteEntretien(Integer id) {
        EntretienDeDecision entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));
        entretien.setDeleted(true);
        entretienRepository.save(entretien);
    }

    public EntretienDeDecisionDTO convertToDTO(EntretienDeDecision entretien) {
        EntretienDeDecisionDTO dto = new EntretienDeDecisionDTO();
        dto.setId(entretien.getId());
        dto.setDate(entretien.getDate());
        dto.setTypeErreur(entretien.getTypeErreur());
        dto.setDetails(entretien.getDetails());
        dto.setDecision(entretien.getDecision());
        dto.setDeleted(entretien.isDeleted());

        if (entretien.getCollaborateur() != null) {
            dto.setCollaborateurId(entretien.getCollaborateur().getId());
            dto.setCollaborateurNom(entretien.getCollaborateur().getNom());
            dto.setCollaborateurPrenom(entretien.getCollaborateur().getPrenom());
        }

        if (entretien.getUser() != null) {
            dto.setUserId(entretien.getUser().getId());
            dto.setUserNom(entretien.getUser().getFirstName());
            dto.setUserPrenom(entretien.getUser().getLastName());
        }

        if (entretien.getValidatedHP() != null) {
            dto.setValidatedHPUserId(entretien.getValidatedHP().getId());
            dto.setValidatedHPNom(entretien.getValidatedHP().getFirstName());
            dto.setValidatedHPPrenom(entretien.getValidatedHP().getLastName());
            dto.setHPValidationDate(entretien.getHPValidationDate());
        }
        if (entretien.getValidatedQM() != null) {
            dto.setValidatedQMUserId(entretien.getValidatedQM().getId());
            dto.setValidatedQMNom(entretien.getValidatedQM().getFirstName());
            dto.setValidatedQMPrenom(entretien.getValidatedQM().getLastName());
            dto.setQMValidationDate(entretien.getQMValidationDate());
        }
        dto.setDateArchivage(entretien.getDateArchivage());
        return dto;
    }
    
    
    public List<EntretienDeDecisionDTO> getEntretiensByCollaborateurId(Integer collaborateurId) {
        // Récupérer d'abord le collaborateur
        Collaborateur collaborateur = collaborateurRepository.findById(collaborateurId)
                .orElseThrow(() -> new RuntimeException("Collaborateur non trouvé"));
        
        // Utiliser la méthode qui filtre à la fois deleted=false et archive=false
        List<EntretienDeDecision> entretiens = entretienRepository.findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        
        return entretiens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<EntretienDeDecision> getArchivedAndNotDeletedEntretiens() {
        return entretienRepository.findArchivedAndNotDeletedEntretiens();
    }
    
    
    public List<EntretienDeDecision> getDeletedNonArchivedEntretiens() {
        return entretienRepository.findByDeletedTrueAndArchiveFalse();
    }

}