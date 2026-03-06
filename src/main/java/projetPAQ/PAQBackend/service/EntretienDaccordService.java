package projetPAQ.PAQBackend.service;

import org.springframework.stereotype.Service;
import projetPAQ.PAQBackend.DTO.EntretienDaccordDTO;
import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienDaccord;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.EntretienDaccordRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EntretienDaccordService {

    private final EntretienDaccordRepository entretienDaccordRepository;

    public EntretienDaccordService(EntretienDaccordRepository entretienDaccordRepository) {
        this.entretienDaccordRepository = entretienDaccordRepository;
    }

    // Récupérer tous les entretiens non supprimés
    public List<EntretienDaccord> getAllEntretiens() {
        return entretienDaccordRepository.findByDeletedFalse();
    }

    // Récupérer un entretien non supprimé par ID
    public Optional<EntretienDaccord> getEntretienById(Integer id) {
        return entretienDaccordRepository.findByIdAndDeletedFalse(id);
    }

    // Créer un entretien (vérification des rôles)
    public EntretienDaccord createEntretien(EntretienDaccord entretien, String userRole) {
        if (!"SL".equals(userRole) && !"SGL".equals(userRole) && !"QMSegment".equals(userRole)) {
            throw new RuntimeException("Seuls les utilisateurs avec le rôle SL, SGL ou QMSegment peuvent ajouter un entretien d'accord.");
        }
        entretien.setDeleted(false); // S'assurer que l'entretien n'est pas marqué comme supprimé
        return entretienDaccordRepository.save(entretien);
    }

    // Mettre à jour un entretien (vérification des rôles et des champs autorisés)
   
    // Supprimer un entretien (soft delete) avec vérification des rôles
    public void deleteEntretien(Integer id, String userRole) {
        if (!"SL".equals(userRole)) {
            throw new RuntimeException("Seuls les utilisateurs avec le rôle SL peuvent supprimer un entretien d'accord.");
        }

        // Récupérer l'entretien par son ID
        Optional<EntretienDaccord> entretienOpt = entretienDaccordRepository.findById(id);

        if (entretienOpt.isPresent()) {
            EntretienDaccord entretien = entretienOpt.get();
            entretien.setDeleted(true); // Marquer l'entretien comme supprimé (soft delete)
            entretienDaccordRepository.save(entretien); // Sauvegarder les modifications
        } else {
            throw new RuntimeException("Entretien non trouvé avec l'ID : " + id);
        }
    }
    
    public List<EntretienDaccord> getDeletedEntretiens() {
        return entretienDaccordRepository.findByDeletedTrue();
    }

    // Récupérer les entretiens d'un collaborateur (non supprimés)
    public List<EntretienDaccord> getEntretiensByCollaborateurId(Integer collaborateurId) {
    	Collaborateur collaborateur = new Collaborateur();
        collaborateur.setId(collaborateurId);
        return entretienDaccordRepository.findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
    }

    

    // Convertir une entité en DTO
    public EntretienDaccordDTO convertToDTO(EntretienDaccord entretien) {
        EntretienDaccordDTO dto = new EntretienDaccordDTO();
        dto.setId(entretien.getId());
        dto.setDate(new java.sql.Date(entretien.getDate().getTime()));
        dto.setTypeErreur(entretien.getTypeErreur());
        dto.setDetails(entretien.getDetails());
        dto.setDecision(entretien.getDecision());
        
        // Collaborateur info
        if (entretien.getCollaborateur() != null) {
            dto.setCollaborateurId(entretien.getCollaborateur().getId());
            dto.setCollaborateurNom(entretien.getCollaborateur().getNom());
            dto.setCollaborateurPrenom(entretien.getCollaborateur().getPrenom());
        }
        
        // User info
        if (entretien.getUser() != null) {
            dto.setUserId(entretien.getUser().getId());
            dto.setUserNom(entretien.getUser().getFirstName());       // Assurez-vous que User a ces champs
            dto.setUserPrenom(entretien.getUser().getLastName()); // Assurez-vous que User a ces champs
        }
        
        if (entretien.getValidatedBy() != null) {
            dto.setValidatedByUserId(entretien.getValidatedBy().getId());
            dto.setValidatedByFirstName(entretien.getValidatedBy().getFirstName());
            dto.setValidatedByLastName(entretien.getValidatedBy().getLastName());
        }
        dto.setValidationDate(entretien.getValidationDate());
        dto.setDateArchivage(entretien.getDateArchivage());
        return dto;
    }
    public EntretienDaccord updateEntretien(Integer id, EntretienDaccord updatedEntretien, String userRole) {
        // Vérifier que l'utilisateur a le droit de modifier l'entretien
    	if (!"SL".equals(userRole) && !"SGL".equals(userRole) && !"QMSegment".equals(userRole)) {
            throw new RuntimeException("Vous n'avez pas les droits pour modifier cet entretien.");
        }

        // Récupérer l'entretien existant
        Optional<EntretienDaccord> existingEntretienOpt = findById(id);
        if (existingEntretienOpt.isEmpty()) {
            throw new RuntimeException("Entretien non trouvé.");
        }

        EntretienDaccord existingEntretien = existingEntretienOpt.get();

        // Mettre à jour uniquement les champs autorisés
        if (updatedEntretien.getDate() != null) {
            existingEntretien.setDate(updatedEntretien.getDate());
        }
        if (updatedEntretien.getTypeErreur() != null) {
            existingEntretien.setTypeErreur(updatedEntretien.getTypeErreur());
        }
        if (updatedEntretien.getDetails() != null) {
            existingEntretien.setDetails(updatedEntretien.getDetails());
        }
        if (updatedEntretien.getDecision() != null) {
            existingEntretien.setDecision(updatedEntretien.getDecision());
        }

        // Sauvegarder les modifications
        return save(existingEntretien);
    }
    
    public Optional<EntretienDaccord> findById(Integer id) {
        return entretienDaccordRepository.findById(id);
    }
    public EntretienDaccord save(EntretienDaccord entretien) {
        return entretienDaccordRepository.save(entretien);
    }
    
    public Optional<EntretienDaccord> getEntretienDetailsById(Integer id) {
        return entretienDaccordRepository.findByIdAndDeletedFalse(id);
    }
    
    
    public EntretienDaccord validateEntretien(Integer id, String userRole, User validator) {
        // Récupérer l'entretien existant
        Optional<EntretienDaccord> existingEntretienOpt = findById(id);
        if (existingEntretienOpt.isEmpty()) {
            throw new RuntimeException("Entretien non trouvé.");
        }

        EntretienDaccord existingEntretien = existingEntretienOpt.get();

        // Valider l'entretien
        existingEntretien.setValidatedBy(validator);
        existingEntretien.setValidationDate(new Date(System.currentTimeMillis()));

        return save(existingEntretien);
    }

 // Récupérer les entretiens non supprimés par userId
    public List<EntretienDaccord> getEntretiensByUserId(Integer userId) {
        return entretienDaccordRepository.findByUserIdAndDeletedFalse(userId);
    }
    
    // Récupérer les entretiens supprimés par userId
    public List<EntretienDaccord> getDeletedEntretiensByUserId(Integer userId) {
        return entretienDaccordRepository.findByUserIdAndDeletedTrue(userId);
    }
    
    public List<EntretienDaccord> getArchivedEntretiens() {
        return entretienDaccordRepository.findByArchiveTrueAndDeletedFalse();
    }
    
    
    public List<EntretienDaccord> getDeletedNonArchivedEntretiens() {
        return entretienDaccordRepository.findByDeletedTrueAndArchiveFalse();
    }

}