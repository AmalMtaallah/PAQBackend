package projetPAQ.PAQBackend.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import projetPAQ.PAQBackend.DTO.EntretienDecisionFinalDTO;
import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienDaccord;
import projetPAQ.PAQBackend.entity.EntretienDeDecision;
import projetPAQ.PAQBackend.entity.EntretienDeMesure;
import projetPAQ.PAQBackend.entity.EntretienDecisionFinal;
import projetPAQ.PAQBackend.entity.EntretienExplicatif;
import projetPAQ.PAQBackend.entity.GroupeRH;
import projetPAQ.PAQBackend.entity.PhaseDialoguePositif;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.CollaborateurRepository;
import projetPAQ.PAQBackend.repository.EntretienDaccordRepository;
import projetPAQ.PAQBackend.repository.EntretienDeDecisionRepository;
import projetPAQ.PAQBackend.repository.EntretienDeMesureRepository;
import projetPAQ.PAQBackend.repository.EntretienDecisionFinalRepository;
import projetPAQ.PAQBackend.repository.EntretienExplicatifRepository;
import projetPAQ.PAQBackend.repository.GroupeRHRepository;
import projetPAQ.PAQBackend.repository.PhaseDialoguePositifRepository;
import projetPAQ.PAQBackend.repository.UserRepository;

@Service
public class EntretienDecisionFinalService {
	private final EntretienDecisionFinalRepository entretienRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final UserRepository userRepository;
    private final GroupeRHRepository groupeRHRepository;
    private final EntretienDaccordRepository entretienDaccordRepository;
    private final EntretienDeDecisionRepository entretienDeDecisionRepository;
    private final EntretienDeMesureRepository entretienDeMesureRepository;
    private final EntretienExplicatifRepository entretienExplicatifRepository;
    private final PhaseDialoguePositifRepository phaseDialoguePositifRepository;

    @Autowired
    public EntretienDecisionFinalService(
            EntretienDecisionFinalRepository entretienRepository,
            CollaborateurRepository collaborateurRepository,
            UserRepository userRepository,
            GroupeRHRepository groupeRHRepository,
            EntretienDaccordRepository entretienDaccordRepository,
            EntretienDeDecisionRepository entretienDeDecisionRepository,
            EntretienDeMesureRepository entretienDeMesureRepository,
            EntretienExplicatifRepository entretienExplicatifRepository,
            PhaseDialoguePositifRepository phaseDialoguePositifRepository) {
        this.entretienRepository = entretienRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.userRepository = userRepository;
        this.groupeRHRepository = groupeRHRepository;
        this.entretienDaccordRepository = entretienDaccordRepository;
        this.entretienDeDecisionRepository = entretienDeDecisionRepository;
        this.entretienDeMesureRepository = entretienDeMesureRepository;
        this.entretienExplicatifRepository = entretienExplicatifRepository;
        this.phaseDialoguePositifRepository = phaseDialoguePositifRepository;
    }



    public EntretienDecisionFinal createEntretien(EntretienDecisionFinal entretien, 
            Integer collaborateurId, Long userId, Long groupeRHId) {
        Collaborateur collaborateur = collaborateurRepository.findById(collaborateurId)
                .orElseThrow(() -> new RuntimeException("Collaborateur non trouvé"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        GroupeRH groupeRH = groupeRHRepository.findById(groupeRHId)
                .orElseThrow(() -> new RuntimeException("Groupe RH non trouvé"));

        entretien.setCollaborateur(collaborateur);
        entretien.setUser(user);
        entretien.setGroupeRH(groupeRH);
        entretien.setValidationComplete(false);

        return entretienRepository.save(entretien);
    }


    @Transactional
    public EntretienDecisionFinal validateByRH(Integer id, Long validateurId) {
        EntretienDecisionFinal entretien = entretienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));

        // Vérifier que le groupe RH existe et a des membres
        if (entretien.getGroupeRH() == null || entretien.getGroupeRH().getMembresIds() == null) {
            throw new IllegalStateException("Aucun groupe RH valide assigné à cet entretien");
        }

        User validateur = userRepository.findById(validateurId)
                .orElseThrow(() -> new RuntimeException("Validateur non trouvé"));

        // Archiver tous les entretiens non archivés et non supprimés du collaborateur
        archiveAllNonArchivedEntretiensForCollaborateur(entretien.getCollaborateur());

        // Mettre à jour la date de dernier archivage du collaborateur
        updateCollaborateurLastArchivingDate(entretien.getCollaborateur());

        entretien.setValidatedRH(validateur);
        entretien.setRHValidationDate(new Date(System.currentTimeMillis()));
        entretien.setValidationComplete(true);
        
        return entretienRepository.save(entretien);
    }
    
    
    private void archiveAllNonArchivedEntretiensForCollaborateur(Collaborateur collaborateur) {
        Date currentDate = new Date(System.currentTimeMillis());
        
        // Archiver tous les types d'entretiens pour ce collaborateur
        archiveEntretiensDaccord(collaborateur, currentDate);
        archiveEntretiensDeDecision(collaborateur, currentDate);
        archiveEntretiensDeMesure(collaborateur, currentDate);
        archiveEntretiensExplicatifs(collaborateur, currentDate);
        archivePhaseDialoguePositif(collaborateur, currentDate);
        archiveEntretiensDecisionFinal(collaborateur, currentDate);
    }

    private void archiveEntretiensDaccord(Collaborateur collaborateur, Date archiveDate) {
        List<EntretienDaccord> entretiens = entretienDaccordRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        entretiens.forEach(e -> {
            e.setArchive(true);
            e.setDateArchivage(archiveDate);
        });
        entretienDaccordRepository.saveAll(entretiens);
    }

    private void archiveEntretiensDeDecision(Collaborateur collaborateur, Date archiveDate) {
        List<EntretienDeDecision> entretiens = entretienDeDecisionRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        entretiens.forEach(e -> {
            e.setArchive(true);
            e.setDateArchivage(archiveDate);
        });
        entretienDeDecisionRepository.saveAll(entretiens);
    }

    private void archiveEntretiensDeMesure(Collaborateur collaborateur, Date archiveDate) {
        List<EntretienDeMesure> entretiens = entretienDeMesureRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        entretiens.forEach(e -> {
            e.setArchive(true);
            e.setDateArchivage(archiveDate);
        });
        entretienDeMesureRepository.saveAll(entretiens);
    }

    private void archiveEntretiensExplicatifs(Collaborateur collaborateur, Date archiveDate) {
        List<EntretienExplicatif> entretiens = entretienExplicatifRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        entretiens.forEach(e -> {
            e.setArchive(true);
            e.setDateArchivage(archiveDate);
        });
        entretienExplicatifRepository.saveAll(entretiens);
    }

    private void archivePhaseDialoguePositif(Collaborateur collaborateur, Date archiveDate) {
        List<PhaseDialoguePositif> entretiens = phaseDialoguePositifRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        entretiens.forEach(e -> {
            e.setArchive(true);
            e.setDateArchivage(archiveDate);
        });
        phaseDialoguePositifRepository.saveAll(entretiens);
    }

    private void archiveEntretiensDecisionFinal(Collaborateur collaborateur, Date archiveDate) {
        List<EntretienDecisionFinal> entretiens = entretienRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        entretiens.forEach(e -> {
            e.setArchive(true);
            e.setDateArchivage(archiveDate);
        });
        entretienRepository.saveAll(entretiens);
    }
    
    private void updateCollaborateurLastArchivingDate(Collaborateur collaborateur) {
        collaborateur.setDernierArchivage(new Date(System.currentTimeMillis()));
        collaborateurRepository.save(collaborateur);
    }
    
    
    
    
    
    

    public EntretienDecisionFinalDTO convertToDTO(EntretienDecisionFinal entretien) {
    	 if (entretien == null) {
             return null;
         }
        EntretienDecisionFinalDTO dto = new EntretienDecisionFinalDTO();
        // Mapping des champs de base
        dto.setId(entretien.getId());
        dto.setDate(entretien.getDate());
        dto.setTypeErreur(entretien.getTypeErreur());
        dto.setDetails(entretien.getDetails());
        dto.setDecisionFinale(entretien.getDecisionFinale());
        dto.setDateArchivage(entretien.getDateArchivage());
        // Collaborateur
        if (entretien.getCollaborateur() != null) {
            dto.setCollaborateurId(entretien.getCollaborateur().getId());
            dto.setCollaborateurNom(entretien.getCollaborateur().getNom());
            dto.setCollaborateurPrenom(entretien.getCollaborateur().getPrenom());
        }
        
        // Créateur
        if (entretien.getUser() != null) {
            dto.setUserId(entretien.getUser().getId());
            dto.setUserNom(entretien.getUser().getFirstName());
            dto.setUserPrenom(entretien.getUser().getLastName());
        }
        
        // Groupe RH
        if (entretien.getGroupeRH() != null) {
            dto.setGroupeRHId(entretien.getGroupeRH().getId());
            dto.setGroupeRHNom(entretien.getGroupeRH().getNom());
            dto.setGroupeRHEmails(entretien.getGroupeRH().getMembresEmails(userRepository));
            dto.setGroupeRHMembresIds(new ArrayList<>(entretien.getGroupeRH().getMembresIds()));
        }
        
        // Validation RH
        if (entretien.getValidatedRH() != null) {
            dto.setValidatedRHUserId(entretien.getValidatedRH().getId());
            dto.setValidatedRHNom(entretien.getValidatedRH().getFirstName());
            dto.setValidatedRHPrenom(entretien.getValidatedRH().getLastName());
            dto.setRHValidationDate(entretien.getRHValidationDate());
        }
        
        dto.setValidationComplete(entretien.isValidationComplete());
        
        return dto;
    }
    
    
 // Récupérer un entretien par son ID
    public EntretienDecisionFinal getById(Integer id) {
        return entretienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));
    }

    // Récupérer les entretiens d'un collaborateur
    public List<EntretienDecisionFinalDTO> getByCollaborateurId(Integer collaborateurId) {
        // Récupérer le collaborateur
        Collaborateur collaborateur = collaborateurRepository.findById(collaborateurId)
                .orElseThrow(() -> new RuntimeException("Collaborateur non trouvé"));
        
        // Utiliser la méthode du repository qui filtre par collaborateur, non supprimé et non archivé
        List<EntretienDecisionFinal> entretiens = entretienRepository.findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        
        return entretiens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Mettre à jour un entretien
    public EntretienDecisionFinal updateEntretienFields(Integer id, EntretienDecisionFinalDTO dto) {
        EntretienDecisionFinal entretien = getById(id);
        
        // Vérifier si l'entretien est déjà validé
       /* if (entretien.isValidationComplete()) {
            throw new IllegalStateException("Impossible de modifier un entretien déjà validé");
        }*/
        
        // Mettre à jour uniquement les champs autorisés
        if (dto.getDate() != null) {
            entretien.setDate(dto.getDate());
        }
        if (dto.getTypeErreur() != null) {
            entretien.setTypeErreur(dto.getTypeErreur());
        }
        if (dto.getDetails() != null) {
            entretien.setDetails(dto.getDetails());
        }
        if (dto.getDecisionFinale() != null) {
            entretien.setDecisionFinale(dto.getDecisionFinale());
        }
        
        return entretienRepository.save(entretien);
    }
    // Suppression logique (corbeille)
    public void softDeleteEntretien(Integer id) {
        EntretienDecisionFinal entretien = getById(id);
        entretien.setDeleted(true);
        entretienRepository.save(entretien);
    }
    
    
    public EntretienDecisionFinal getByIdAndNotDeleted(Integer id) {
        return entretienRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé ou supprimé"));
    }
    
    
    public List<EntretienDecisionFinal> getArchivedAndNotDeletedEntretiens() {
        return entretienRepository.findArchivedAndNotDeletedEntretiens();
    }
    
    
    public List<EntretienDecisionFinal> getDeletedNonArchivedEntretiens() {
        return entretienRepository.findByDeletedTrueAndArchiveFalse();
    }

}