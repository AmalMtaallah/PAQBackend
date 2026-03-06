package projetPAQ.PAQBackend.service;



import projetPAQ.PAQBackend.DTO.CollaborateurCreatorInfoDTO;
import projetPAQ.PAQBackend.DTO.VerificationResponse;
import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienExplicatif;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.CollaborateurRepository;
import projetPAQ.PAQBackend.repository.EntretienExplicatifRepository;
import projetPAQ.PAQBackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollaborateurService {

    private final CollaborateurRepository collaborateurRepository;
   
    @Autowired
    private EntretienExplicatifRepository entretienExplicatifRepository;
    
    @Autowired 
    private UserRepository userRepository;

    public CollaborateurService(CollaborateurRepository collaborateurRepository) {
        this.collaborateurRepository = collaborateurRepository;
    }

    public List<Collaborateur> getAllCollaborateurs() {
        return collaborateurRepository.findAll();
    }

    public Collaborateur getCollaborateurById(Integer id) {
        return collaborateurRepository.findById(id).orElse(null);
    }

   /* public Collaborateur createCollaborateur(Collaborateur collaborateur, String userRole) {
        if (!"SL".equals(userRole)) {
            throw new RuntimeException("Seuls les utilisateurs avec le rôle SL peuvent ajouter un collaborateur.");
        }
        return collaborateurRepository.save(collaborateur);
    }*/

    public Optional<Collaborateur> updateCollaborateur(Integer id, Collaborateur newCollaborateur) {
        return collaborateurRepository.findById(id).map(collaborateur -> {
            collaborateur.setNom(newCollaborateur.getNom());
            collaborateur.setPrenom(newCollaborateur.getPrenom());
            collaborateur.setDateEmbauche(newCollaborateur.getDateEmbauche());
            collaborateur.setSeg(newCollaborateur.getSeg());
            return collaborateurRepository.save(collaborateur);
        });
    }

    public void deleteCollaborateur(Integer id) {
        collaborateurRepository.deleteById(id);
    }
    
    public List<Collaborateur> getCollaborateursByUserId(Long userId) {
        return collaborateurRepository.findByUserId(userId);}
    
    
    
    public Collaborateur createCollaborateur(Collaborateur collaborateur, String userRole) {
        if (!"SL".equals(userRole)) {
            throw new RuntimeException("Seuls les utilisateurs avec le rôle SL peuvent ajouter un collaborateur.");
        }

        // Vérifier si l'ID est fourni et unique
        if (collaborateur.getId() == null) {
            throw new RuntimeException("ID is required");
        }

        if (collaborateurRepository.existsById(collaborateur.getId())) {
            throw new RuntimeException("ID must be unique");
        }

        return collaborateurRepository.save(collaborateur);
    }
    
    
    
  

  /*  public ResponseEntity<?> deleteCollaborateur(Integer id, String token) {
        // Vérifiez si le collaborateur a des entretiens associés
        List<EntretienExplicatif> entretiens = entretienExplicatifRepository.findByCollaborateurId(id);
        if (!entretiens.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Le collaborateur a des entretiens associés, suppression impossible");
        }

        // Marquer le collaborateur comme supprimé (archivé)
        collaborateurRepository.softDeleteById(id);

        return ResponseEntity.ok("Collaborateur archivé avec succès");
    }

    // Récupérer les collaborateurs actifs
    public List<Collaborateur> getCollaborateursActifs() {
        return collaborateurRepository.findByDeletedFalse();
    }

    // Récupérer les collaborateurs supprimés (corbeille)
    public List<Collaborateur> getCollaborateursSupprimes() {
        return collaborateurRepository.findByDeletedTrue();
    }
    */
    
    
    
    
    public boolean isSglCreatorOfCollaborateur(Long sglId, Integer collaborateurId) {
        // 1. Trouver le SL qui a créé le collaborateur
        User slCreator = collaborateurRepository.findUserCreatorByCollaborateurId(collaborateurId)
                .orElseThrow(() -> new RuntimeException("Collaborateur ou créateur SL non trouvé"));

        // 2. Vérifier si le SL a été créé par le SGL spécifié
        return slCreator.getCreatedBy() != null && slCreator.getCreatedBy().equals(sglId);
    }
    public VerificationResponse verifySglCreationChain(Long sglId, Integer collaborateurId) {
        VerificationResponse response = new VerificationResponse();
        response.setSglId(sglId);
        response.setCollaborateurId(collaborateurId);

        try {
            // 1. Vérifier que l'utilisateur sglId est bien un SGL
            User sgl = userRepository.findById(sglId)
                    .orElseThrow(() -> new RuntimeException("SGL non trouvé"));
            
            if (!"SGL".equals(sgl.getRole())) {
                response.setValid(false);
                response.setMessage("L'utilisateur spécifié n'est pas un SGL");
                return response;
            }

            // 2. Trouver le SL qui a créé le collaborateur
            User slCreator = collaborateurRepository.findUserCreatorByCollaborateurId(collaborateurId)
                    .orElseThrow(() -> new RuntimeException("Collaborateur ou créateur SL non trouvé"));

            response.setSlCreatorId(slCreator.getId());

            // 3. Vérifier la chaîne de création
            if (slCreator.getCreatedBy() == null) {
                response.setValid(false);
                response.setMessage("Le SL créateur n'a pas de SGL parent");
                return response;
            }

            if (!slCreator.getCreatedBy().equals(sglId)) {
                response.setValid(false);
                response.setMessage("Le SL créateur n'a pas été créé par ce SGL");
                return response;
            }

            response.setValid(true);
            response.setMessage("Chaîne de création vérifiée: SGL → SL → Collaborateur");
            return response;

        } catch (Exception e) {
            response.setValid(false);
            response.setMessage("Erreur de vérification: " + e.getMessage());
            return response;
        }
    }
    
    
    public ResponseEntity<?> updateCollaborateurUserId(Integer collaborateurId, Long newUserId, String token) {
        try {
            // Vérification du token et des autorisations
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide");
            }

            // Vérifier que le collaborateur existe
            Optional<Collaborateur> collaborateurOpt = collaborateurRepository.findById(collaborateurId);
            if (!collaborateurOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collaborateur non trouvé");
            }

            // Vérifier que le nouvel utilisateur existe
            Optional<User> newUserOpt = userRepository.findById(newUserId);
            if (!newUserOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nouvel utilisateur non trouvé");
            }

            // Vérifier que le nouvel utilisateur a le rôle SL
            User newUser = newUserOpt.get();
            if (!"SL".equals(newUser.getRole())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le nouvel utilisateur doit être un SL");
            }

            // Vérifier que le nouvel utilisateur (SL) est créé par le même SGL
            Long sglId = collaborateurOpt.get().getUser().getCreatedBy();
            if (newUser.getCreatedBy() == null || !newUser.getCreatedBy().equals(sglId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le nouvel utilisateur (SL) doit être créé par le même SGL");
            }

            // Mettre à jour le userId du collaborateur
            collaborateurRepository.updateCollaborateurUserId(collaborateurId, newUser);

            return ResponseEntity.ok("Propriétaire du collaborateur mis à jour avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour");
        }
    }


    
    
    
    public CollaborateurCreatorInfoDTO getCollaborateurCreatorInfo(Integer collaborateurId) {
        // 1. Récupérer le collaborateur et vérifier son existence
        Collaborateur collaborateur = collaborateurRepository.findById(collaborateurId)
                .orElseThrow(() -> new RuntimeException("Collaborateur non trouvé avec l'ID: " + collaborateurId));

        // 2. Récupérer le SL (User) qui a créé le collaborateur
        User slCreator = collaborateur.getUser();
        if (slCreator == null) {
            throw new RuntimeException("Aucun SL trouvé pour ce collaborateur");
        }

        // 3. Récupérer le SGL qui a créé le SL (via createdBy)
        Long sglId = slCreator.getCreatedBy();
        if (sglId == null) {
            throw new RuntimeException("Le SL n'a pas de SGL parent");
        }

        User sglCreator = userRepository.findById(sglId)
                .orElseThrow(() -> new RuntimeException("SGL non trouvé avec l'ID: " + sglId));

        // 4. Construire la réponse DTO
        CollaborateurCreatorInfoDTO response = new CollaborateurCreatorInfoDTO();
        response.setSlId(slCreator.getId());
        response.setSlFirstName(slCreator.getFirstName());
        response.setSlLastName(slCreator.getLastName());
        response.setSglId(sglCreator.getId());
        response.setSglFirstName(sglCreator.getFirstName());
        response.setSglLastName(sglCreator.getLastName());

        return response;
    }
    
    
    
    public ResponseEntity<?> updateCollaborateurAndUser(Integer collaborateurId, Long newUserId, Long newCreatedBy) {
        try {
            // Vérifier que le collaborateur existe
            Optional<Collaborateur> collaborateurOpt = collaborateurRepository.findById(collaborateurId);
            if (!collaborateurOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collaborateur non trouvé");
            }

            // Vérifier que le nouvel utilisateur existe
            Optional<User> newUserOpt = userRepository.findById(newUserId);
            if (!newUserOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nouvel utilisateur non trouvé");
            }

            User newUser = newUserOpt.get();

            // Vérifier que le nouvel utilisateur a le rôle SL
            if (!"SL".equals(newUser.getRole())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le nouvel utilisateur doit être un SL");
            }

            // Vérifier que le nouvel utilisateur (SL) est créé par le même SGL
            if (newUser.getCreatedBy() == null || !newUser.getCreatedBy().equals(newCreatedBy)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le nouvel utilisateur (SL) doit être créé par le même SGL");
            }

            // Mettre à jour le userId du collaborateur
            Collaborateur collaborateur = collaborateurOpt.get();
            collaborateur.setUser(newUser);
            collaborateurRepository.save(collaborateur);

            // Mettre à jour le createdBy du nouvel utilisateur
            newUser.setCreatedBy(newCreatedBy);
            userRepository.save(newUser);

            return ResponseEntity.ok("Mise à jour effectuée avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour");
        }
    }
}
