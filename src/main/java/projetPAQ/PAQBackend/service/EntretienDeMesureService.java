package projetPAQ.PAQBackend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import projetPAQ.PAQBackend.DTO.EntretienDeMesureDTO;
import projetPAQ.PAQBackend.entity.EntretienDeMesure;
import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.EntretienDeMesureRepository;
import projetPAQ.PAQBackend.repository.CollaborateurRepository;
import projetPAQ.PAQBackend.repository.UserRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EntretienDeMesureService {

    private final EntretienDeMesureRepository entretienDeMesureRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final UserRepository userRepository;

    @Autowired
    public EntretienDeMesureService(
            EntretienDeMesureRepository entretienDeMesureRepository,
            CollaborateurRepository collaborateurRepository,
            UserRepository userRepository) {
        this.entretienDeMesureRepository = entretienDeMesureRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.userRepository = userRepository;
    }

    // Récupérer tous les entretiens non supprimés
    public List<EntretienDeMesure> getAllEntretiens() {
        return entretienDeMesureRepository.findByDeletedFalse();
    }

    // Récupérer un entretien non supprimé par ID
    public Optional<EntretienDeMesure> getEntretienById(Integer id) {
        return entretienDeMesureRepository.findByIdAndDeletedFalse(id);
    }

    // Créer un entretien
    public EntretienDeMesure createEntretien(EntretienDeMesure entretien, Integer collaborateurId, Long userId) {
        // Récupérer le collaborateur depuis la base de données
        Collaborateur collaborateur = collaborateurRepository.findById(collaborateurId)
                .orElseThrow(() -> new RuntimeException("Collaborateur non trouvé avec l'ID : " + collaborateurId));

        // Récupérer l'utilisateur depuis la base de données
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId));

        // Associer le collaborateur et l'utilisateur à l'entretien
        entretien.setCollaborateur(collaborateur);
        entretien.setUser(user);

        // S'assurer que l'entretien n'est pas marqué comme supprimé
        entretien.setDeleted(false);

        // Sauvegarder l'entretien
        return entretienDeMesureRepository.save(entretien);
    }

    // Mettre à jour un entretien
    /*public EntretienDeMesure updateEntretien(Integer id, EntretienDeMesureDTO entretienDTO) {
        // Récupérer l'entretien existant
        Optional<EntretienDeMesure> existingEntretienOpt = entretienDeMesureRepository.findById(id);
        if (existingEntretienOpt.isEmpty()) {
            throw new RuntimeException("Entretien non trouvé avec l'ID : " + id);
        }

        EntretienDeMesure existingEntretien = existingEntretienOpt.get();

        // Mettre à jour uniquement les champs autorisés
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

        // Sauvegarder les modifications
        return entretienDeMesureRepository.save(existingEntretien);
    }
*/
    
    public EntretienDeMesure updateEntretien(Integer id, EntretienDeMesureDTO entretienDTO) {
        // Récupérer l'entretien existant
        Optional<EntretienDeMesure> existingEntretienOpt = entretienDeMesureRepository.findById(id);
        if (existingEntretienOpt.isEmpty()) {
            throw new RuntimeException("Entretien non trouvé avec l'ID : " + id);
        }

        EntretienDeMesure existingEntretien = existingEntretienOpt.get();

        // Mettre à jour uniquement les champs autorisés
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
        existingEntretien.setValidatedQMseegmet(null);
        existingEntretien.setValidatedSGL(null);
        existingEntretien.setQmseegmetValidationDate(null);
        existingEntretien.setSglValidationDate(null);

        // Sauvegarder les modifications
        return entretienDeMesureRepository.save(existingEntretien);
    }

    // Supprimer un entretien (soft delete)
    public void deleteEntretien(Integer id) {
        Optional<EntretienDeMesure> entretienOpt = entretienDeMesureRepository.findById(id);
        if (entretienOpt.isPresent()) {
            EntretienDeMesure entretien = entretienOpt.get();
            entretien.setDeleted(true); // Marquer l'entretien comme supprimé
            entretienDeMesureRepository.save(entretien);
        } else {
            throw new RuntimeException("Entretien non trouvé avec l'ID : " + id);
        }
    }

    // Valider l'entretien par QMseegmet
   public EntretienDeMesure validateByQMseegmet(Integer id, String email) {
    Optional<EntretienDeMesure> entretienOpt = entretienDeMesureRepository.findById(id);
    if (entretienOpt.isEmpty()) {
        throw new RuntimeException("Entretien non trouvé avec l'ID : " + id);
    }

    User validatedQMseegmet = userRepository.findByEmail(email);
    if (validatedQMseegmet == null) {
        throw new RuntimeException("Utilisateur non trouvé avec l'email : " + email);
    }

    EntretienDeMesure entretien = entretienOpt.get();
    entretien.setValidatedQMseegmet(validatedQMseegmet);
    entretien.setQmseegmetValidationDate(new Date(System.currentTimeMillis())); // Date actuelle
    
    return entretienDeMesureRepository.save(entretien);
}

public EntretienDeMesure validateBySGL(Integer id, String email) {
    Optional<EntretienDeMesure> entretienOpt = entretienDeMesureRepository.findById(id);
    if (entretienOpt.isEmpty()) {
        throw new RuntimeException("Entretien non trouvé avec l'ID : " + id);
    }

    User validatedSGL = userRepository.findByEmail(email);
    if (validatedSGL == null) {
        throw new RuntimeException("Utilisateur non trouvé avec l'email : " + email);
    }

    EntretienDeMesure entretien = entretienOpt.get();
    entretien.setValidatedSGL(validatedSGL);
    entretien.setSglValidationDate(new Date(System.currentTimeMillis())); // Date actuelle
    
    return entretienDeMesureRepository.save(entretien);
}
    // Convertir une entité en DTO
    public EntretienDeMesureDTO convertToDTO(EntretienDeMesure entretien) {
        EntretienDeMesureDTO dto = new EntretienDeMesureDTO();
        dto.setId(entretien.getId());
        dto.setDate(entretien.getDate());
        dto.setTypeErreur(entretien.getTypeErreur());
        dto.setDetails(entretien.getDetails());
        dto.setDecision(entretien.getDecision());
        
        // Collaborateur info
        if (entretien.getCollaborateur() != null) {
            dto.setCollaborateurId(entretien.getCollaborateur().getId());
            dto.setCollaborateurNom(entretien.getCollaborateur().getNom());
            dto.setCollaborateurPrenom(entretien.getCollaborateur().getPrenom());
        }
        
        // User (superviseur) info
        if (entretien.getUser() != null) {
            dto.setUserId(entretien.getUser().getId());
            dto.setUserNom(entretien.getUser().getFirstName());
            dto.setUserPrenom(entretien.getUser().getLastName());
        }
        
        // Validated QMseegmet info
        if (entretien.getValidatedQMseegmet() != null) {
            dto.setValidatedQMseegmetUserId(entretien.getValidatedQMseegmet().getId());
            dto.setValidatedQMseegmetNom(entretien.getValidatedQMseegmet().getFirstName());
            dto.setValidatedQMseegmetPrenom(entretien.getValidatedQMseegmet().getLastName());
        }
        
        // Validated SGL info
        if (entretien.getValidatedSGL() != null) {
            dto.setValidatedSGLUserId(entretien.getValidatedSGL().getId());
            dto.setValidatedSGLNom(entretien.getValidatedSGL().getFirstName());
            dto.setValidatedSGLPrenom(entretien.getValidatedSGL().getLastName());
        }
        
        dto.setQmseegmetValidationDate(entretien.getQmseegmetValidationDate());
        dto.setSglValidationDate(entretien.getSglValidationDate());
        dto.setDateArchivage(entretien.getDateArchivage());
        dto.setDeleted(entretien.isDeleted());
        return dto;
    }

    // Récupérer les entretiens non supprimés par userId
    public List<EntretienDeMesure> getEntretiensByUserId(Integer userId) {
        return entretienDeMesureRepository.findByUserIdAndDeletedFalse(userId);
    }

    // Récupérer les entretiens supprimés par userId
    public List<EntretienDeMesure> getDeletedEntretiensByUserId(Integer userId) {
        return entretienDeMesureRepository.findByUserIdAndDeletedTrue(userId);
    }
    
    
    
 // Récupérer un entretien par collaborateurId, non supprimé et non archivé
    public Optional<EntretienDeMesure> getEntretienByCollaborateurId(Integer collaborateurId) {
        return entretienDeMesureRepository.findFirstByCollaborateurIdAndDeletedFalseAndArchiveFalse(collaborateurId);
    }
    
    
    public List<EntretienDeMesure> getArchivedAndNotDeletedEntretiens() {
        return entretienDeMesureRepository.findArchivedAndNotDeletedEntretiens();
    }
    
    
    public List<EntretienDeMesure> getDeletedNonArchivedEntretiens() {
        return entretienDeMesureRepository.findByDeletedTrueAndArchiveFalse();
    }

}