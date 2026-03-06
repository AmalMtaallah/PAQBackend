package projetPAQ.PAQBackend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienExplicatif;
import projetPAQ.PAQBackend.service.EntretienExplicatifService;
import projetPAQ.PAQBackend.DTO.EntretienExplicatifDTO;
import projetPAQ.PAQBackend.configuration.JwtUtils;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.CollaborateurRepository;
import projetPAQ.PAQBackend.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entretiensExplicatif")
public class EntretienExplicatifController {

    private final EntretienExplicatifService entretienExplicatifService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    @Autowired
    private CollaborateurRepository collaborateurRepository;

    public EntretienExplicatifController(EntretienExplicatifService entretienExplicatifService, 
                                         JwtUtils jwtUtils, 
                                         UserRepository userRepository) {
        this.entretienExplicatifService = entretienExplicatifService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEntretien(@RequestBody EntretienExplicatif entretien, 
                                          @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
            }

            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
            }

            User user = userOptional.get();
            String userRole = user.getRole(); // Assurez-vous que votre entité User a un champ `role`

            if (!"SL".equals(userRole) && !"SGL".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Seuls les utilisateurs avec le rôle SL ou SGL peuvent ajouter un entretien explicatif.");
            }

            entretien.setUser(user);
            EntretienExplicatif savedEntretien = entretienExplicatifService.createEntretien(entretien, userRole);
            EntretienExplicatifDTO dto = entretienExplicatifService.convertToDTO(savedEntretien);

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }

    @GetMapping
    public ResponseEntity<List<EntretienExplicatifDTO>> getAllEntretiens(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer la liste des entretiens
            List<EntretienExplicatif> entretiens = entretienExplicatifService.getAllEntretiens();

            // Convertir la liste en DTOs
            List<EntretienExplicatifDTO> entretienDTOs = entretiens.stream()
                .map(entretienExplicatifService::convertToDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(entretienDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntretienExplicatifDTO> getEntretienById(@PathVariable Integer id, 
                                                                  @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer l'entretien par son ID
            Optional<EntretienExplicatif> entretien = entretienExplicatifService.getEntretienById(id);

            // Convertir l'entretien en DTO
            return entretien.map(e -> ResponseEntity.ok(entretienExplicatifService.convertToDTO(e)))
                           .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   /* @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEntretien(@PathVariable Integer id, 
                                             @RequestBody EntretienExplicatif newEntretien, 
                                             @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
            }

            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
            }

            User user = userOptional.get();
            String userRole = user.getRole();

            if (!"SL".equals(userRole) && !"SGL".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Seuls les utilisateurs avec le rôle SL ou SGL peuvent mettre à jour un entretien explicatif.");
            }

            Optional<EntretienExplicatif> updatedEntretien = entretienExplicatifService.updateEntretien(id, newEntretien);
            return updatedEntretien.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }*/
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEntretien(@PathVariable Integer id, 
                                             @RequestBody EntretienExplicatifDTO newEntretienDTO, 
                                             @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
            }

            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
            }

            User user = userOptional.get();
            String userRole = user.getRole();

            if (!"SL".equals(userRole) && !"SGL".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Seuls les utilisateurs avec le rôle SL ou SGL peuvent mettre à jour un entretien explicatif.");
            }

            // Vérifier si l'entretien existe
            Optional<EntretienExplicatif> existingEntretienOptional = entretienExplicatifService.findById(id);
            if (!existingEntretienOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entretien explicatif not found");
            }

            EntretienExplicatif existingEntretien = existingEntretienOptional.get();

            // Mise à jour des champs
            if (newEntretienDTO.getDate() != null) {
                existingEntretien.setDate(newEntretienDTO.getDate());
            }
            if (newEntretienDTO.getTypeErreur() != null) {
                existingEntretien.setTypeErreur(newEntretienDTO.getTypeErreur());
            }
            if (newEntretienDTO.getDetails() != null) {
                existingEntretien.setDetails(newEntretienDTO.getDetails());
            }
            if (newEntretienDTO.getDecision() != null) {
                existingEntretien.setDecision(newEntretienDTO.getDecision());
            }

            // Appeler le service pour sauvegarder la mise à jour
            Optional<EntretienExplicatif> updatedEntretienOptional = entretienExplicatifService.updateEntretien(id, existingEntretien);

            if (!updatedEntretienOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour de l'entretien explicatif.");
            }

            EntretienExplicatifDTO dto = entretienExplicatifService.convertToDTO(updatedEntretienOptional.get());
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }


    /*@DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEntretien(@PathVariable Integer id, 
                                             @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
            }

            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
            }

            User user = userOptional.get();
            String userRole = user.getRole();

            if (!"SL".equals(userRole) && !"SGL".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Seuls les utilisateurs avec le rôle SL ou SGL peuvent supprimer un entretien explicatif.");
            }

            entretienExplicatifService.deleteEntretien(id);
            return ResponseEntity.ok("Entretien supprimé avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    */
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEntretien(@PathVariable Integer id, 
                                             @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token et des autorisations...
        	 if (token == null || !token.startsWith("Bearer ")) {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
             }

             String jwtToken = token.substring(7);
             String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

             if (email == null || email.isEmpty()) {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
             }

             Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

             if (!userOptional.isPresent()) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
             }

             User user = userOptional.get();
             String userRole = user.getRole();

             if (!"SL".equals(userRole) && !"SGL".equals(userRole)) {
                 return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Seuls les utilisateurs avec le rôle SL ou SGL peuvent supprimer un entretien explicatif.");
             }
            entretienExplicatifService.deleteEntretien(id);
            return ResponseEntity.ok("Entretien marqué comme supprimé avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<?> restoreEntretien(@PathVariable Integer id, 
                                              @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token et des autorisations...
        	 if (token == null || !token.startsWith("Bearer ")) {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
             }

             String jwtToken = token.substring(7);
             String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

             if (email == null || email.isEmpty()) {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
             }

             Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

             if (!userOptional.isPresent()) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
             }

             User user = userOptional.get();
             String userRole = user.getRole();

             if (!"SL".equals(userRole) && !"SGL".equals(userRole)) {
                 return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Seuls les utilisateurs avec le rôle SL ou SGL peuvent supprimer un entretien explicatif.");
             }

            entretienExplicatifService.restoreEntretien(id);
            return ResponseEntity.ok("Entretien restauré avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    
    /*@GetMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<List<EntretienExplicatifDTO>> getEntretiensByCollaborateurId(@PathVariable Integer collaborateurId, 
                                                                                      @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer les entretiens par l'ID du collaborateur
            List<EntretienExplicatif> entretiens = entretienExplicatifService.getEntretiensByCollaborateurId(collaborateurId);

            // Convertir la liste en DTOs
            List<EntretienExplicatifDTO> entretienDTOs = entretiens.stream()
                .map(entretienExplicatifService::convertToDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(entretienDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }*/
    
   /* @GetMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<?> getEntretiensByCollaborateurId(@PathVariable Integer collaborateurId, 
                                                            @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Token is missing or invalid");
            }

            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found");
            }

            // Vérifier si le collaborateur existe
            if (!collaborateurRepository.existsById(collaborateurId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collaborateur not found with id: " + collaborateurId);
            }

            // Récupérer les entretiens par l'ID du collaborateur
            List<EntretienExplicatif> entretiens = entretienExplicatifService.getEntretiensByCollaborateurId(collaborateurId);

            // Convertir la liste en DTOs
            List<EntretienExplicatifDTO> entretienDTOs = entretiens.stream()
                .map(entretienExplicatifService::convertToDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(entretienDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }*/
    
    @GetMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<?> getEntretiensByCollaborateurId(@PathVariable Integer collaborateurId, 
                                                            @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Token is missing or invalid");
            }

            String jwtToken = token.substring(7); // Retirer "Bearer "
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
            }

            // Vérification de l'utilisateur
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found");
            }

            // Vérifier si le collaborateur existe et n'est pas supprimé
            Optional<Collaborateur> collaborateurOptional = collaborateurRepository.findById(collaborateurId);
            if (!collaborateurOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collaborateur not found with id: " + collaborateurId);
            }

            Collaborateur collaborateur = collaborateurOptional.get();
            if (collaborateur.isDeleted()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collaborateur is deleted: " + collaborateurId);
            }

            // Récupérer les entretiens par l'ID du collaborateur
            List<EntretienExplicatif> entretiens = entretienExplicatifService.getEntretiensByCollaborateurId(collaborateurId);

            // Convertir la liste en DTOs
            List<EntretienExplicatifDTO> entretienDTOs = entretiens.stream()
                .filter(entretien -> !entretien.isDeleted())
                .map(entretienExplicatifService::convertToDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(entretienDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }
    @PutMapping("/validate/{id}")
    public ResponseEntity<?> validateEntretien(@PathVariable Integer id, 
                                              @RequestHeader("Authorization") String token) {
        try {
            // Vérifier le token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
            }

            // Extraire l'email de l'utilisateur à partir du token
            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
            }

            // Récupérer l'utilisateur à partir de l'email
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
            }

            User user = userOptional.get();
            String userRole = user.getRole();
           /* if (!"SL".equals(userRole) && !"SGL".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Seuls les utilisateurs avec le rôle SL ou SGL peuvent valider un entretien explicatif.");
            }*/

            // Valider l'entretien
            // Valider l'entretien en passant l'utilisateur
            Optional<EntretienExplicatif> validatedEntretien = entretienExplicatifService.validateEntretien(id, user);

            if (validatedEntretien.isPresent()) {
                EntretienExplicatifDTO dto = entretienExplicatifService.convertToDTO(validatedEntretien.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entretien not found with id: " + id);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    } 
    
    
    
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getEntretiensByUserId(@PathVariable Integer userId, 
                                                   @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Token is missing or invalid");
            }

            String jwtToken = token.substring(7); // Retirer "Bearer "
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
            }

            // Vérification de l'utilisateur
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found");
            }

            // Récupérer les entretiens par l'ID de l'utilisateur et non supprimés
            List<EntretienExplicatif> entretiens = entretienExplicatifService.getEntretiensByUserIdAndNotDeleted(userId);

            // Convertir la liste en DTOs
            List<EntretienExplicatifDTO> entretienDTOs = entretiens.stream()
                .map(entretienExplicatifService::convertToDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(entretienDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/user/{userId}/deleted")
    public ResponseEntity<?> getDeletedEntretiensByUserId(@PathVariable Integer userId, 
                                                         @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Token is missing or invalid");
            }

            String jwtToken = token.substring(7); // Retirer "Bearer "
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
            }

            // Vérification de l'utilisateur
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found");
            }

            // Récupérer les entretiens supprimés par l'ID de l'utilisateur
            List<EntretienExplicatif> deletedEntretiens = entretienExplicatifService.getDeletedEntretiensByUserId(userId);

            // Convertir la liste en DTOs
            List<EntretienExplicatifDTO> deletedEntretienDTOs = deletedEntretiens.stream()
                .map(entretienExplicatifService::convertToDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(deletedEntretienDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }
    
    
    
    
    @GetMapping("/archived")
    public ResponseEntity<?> getArchivedEntretiens(@RequestHeader("Authorization") String token) {
        try {
            // Vérification du token (comme dans vos autres méthodes)
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Token is missing or invalid");
            }

            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found");
            }

            // Récupérer les entretiens archivés
            List<EntretienExplicatifDTO> archivedEntretiens = entretienExplicatifService.getArchivedEntretiensDTO();
            return ResponseEntity.ok(archivedEntretiens);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/corbeille")
    public ResponseEntity<?> getDeletedAndNotArchivedEntretiens(@RequestHeader("Authorization") String token) {
        try {
            // Validation du token (comme dans vos autres méthodes)
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide");
            }

            // Récupération des entretiens
            List<EntretienExplicatif> entretiens = entretienExplicatifService.getDeletedAndNotArchivedEntretiens();
            
            // Conversion en DTO
            List<EntretienExplicatifDTO> dtos = entretiens.stream()
                .map(entretienExplicatifService::convertToDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }
    }
}