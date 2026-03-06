package projetPAQ.PAQBackend.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Claims;
import projetPAQ.PAQBackend.DTO.CollaborateurCreatorInfoDTO;
import projetPAQ.PAQBackend.DTO.CollaborateurDTO;
import projetPAQ.PAQBackend.DTO.UserDTO;
import projetPAQ.PAQBackend.DTO.VerificationResponse;
import projetPAQ.PAQBackend.configuration.JwtUtils;
import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienExplicatif;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.CollaborateurRepository;
import projetPAQ.PAQBackend.repository.EntretienExplicatifRepository;
import projetPAQ.PAQBackend.repository.UserRepository;
import projetPAQ.PAQBackend.service.CollaborateurService;
import projetPAQ.PAQBackend.service.CollaborateurUpdateRequest;

@RestController
@RequestMapping("/api/collaborateurs")
public class CollaborateurController {

    private final CollaborateurRepository collaborateurRepository;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    @Autowired
    private final EntretienExplicatifRepository entretienExplicatifRepository;
    @Autowired
    private CollaborateurService collaborateurService;
   

    public CollaborateurController(CollaborateurRepository collaborateurRepository, 
                                   JwtUtils jwtUtils, 
                                   UserRepository userRepository) {
        this.collaborateurRepository = collaborateurRepository;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
		this.entretienExplicatifRepository = null;
		
    }

   /* @PostMapping("/add")
    public ResponseEntity<?> addCollaborateur(@RequestBody Collaborateur collaborateur, 
                                              @RequestHeader("Authorization") String token) {
        try {
            // Vérifiez si le token est valide
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
            }

            String jwtToken = token.substring(7); // Retirer le préfixe "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken); // Extraire les claims du token

            // Extraire l'email du subject du token
            String email = claims.getSubject(); // Utiliser getSubject() pour obtenir l'email
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
            }

            // Rechercher l'utilisateur par email dans la base de données
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
            }

            User user = userOptional.get(); // Récupérer l'utilisateur s'il existe

            // Définir l'utilisateur (créateur) du collaborateur
            collaborateur.setUser(user);

            // Enregistrer le collaborateur avec la référence de l'utilisateur
            collaborateurRepository.save(collaborateur);

            return ResponseEntity.ok("Collaborator added successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }*/
    
    
    @PostMapping("/add")
    public ResponseEntity<?> addCollaborateur(@RequestBody Collaborateur collaborateur,
                                              @RequestHeader("Authorization") String token) {
        try {
            // Vérifiez si le token est valide
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
            }

            String jwtToken = token.substring(7); // Retirer le préfixe "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken); // Extraire les claims du token

            // Extraire l'email du subject du token
            String email = claims.getSubject(); // Utiliser getSubject() pour obtenir l'email
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
            }

            // Rechercher l'utilisateur par email dans la base de données
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
            }

            User user = userOptional.get(); // Récupérer l'utilisateur s'il existe

            // Vérifier si l'ID est fourni et unique
            if (collaborateur.getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID is required");
            }

            if (collaborateurRepository.existsById(collaborateur.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID must be unique");
            }

            // Définir la date de dernier archivage à la date d'embauche
            collaborateur.setDernierArchivage(collaborateur.getDateEmbauche());

            // Définir l'utilisateur (créateur) du collaborateur
            collaborateur.setUser(user);

            // Enregistrer le collaborateur avec la référence de l'utilisateur
            collaborateurRepository.save(collaborateur);

            return ResponseEntity.ok("Collaborator added successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    
    @PutMapping("/update")
    public ResponseEntity<?> updateCollaborateur(@RequestBody CollaborateurUpdateRequest request, 
                                                @RequestHeader("Authorization") String token) {
        Integer id = request.getId();
        Collaborateur collaborateur = request.getCollaborateur();
        System.out.println("Début de la mise à jour du collaborateur avec ID: " + id);
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println("Token est manquant ou malformé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token est manquant ou malformé");
            }

            String jwtToken = token.substring(7); // Retirer le préfixe "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken); // Extraire les claims du token
            System.out.println("Claims du token: " + claims);

            // Vérification de l'email
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                System.out.println("L'email est manquant dans le token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email est manquant dans le token");
            }

            System.out.println("Email extrait du token: " + email);

            // Vérification de l'expiration du token
            Date expirationDate = claims.getExpiration();
            System.out.println("Date d'expiration du token: " + expirationDate);
            if (expirationDate != null && expirationDate.before(new Date())) {
                System.out.println("Le token a expiré");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expiré");
            }

            // Vérification du rôle
            String role = (String) claims.get("role");
            System.out.println("Rôle extrait du token: " + role);
            if (!"ROLE_SL".equals(role)) {
                System.out.println("L'utilisateur n'a pas le rôle requis pour la mise à jour");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas le rôle requis pour effectuer cette action");
            }

            // Rechercher l'utilisateur par email
            Optional<User> userOptional = Optional.of(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                System.out.println("Utilisateur non trouvé pour l'email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur avec cet email non trouvé");
            }

            User user = userOptional.get();
            System.out.println("Utilisateur trouvé: " + user);

            // Rechercher le collaborateur
            Optional<Collaborateur> collaborateurOptional = collaborateurRepository.findById(id);
            if (!collaborateurOptional.isPresent()) {
                System.out.println("Collaborateur non trouvé pour l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collaborateur non trouvé");
            }

            Collaborateur existingCollaborateur = collaborateurOptional.get();
            System.out.println("Collaborateur trouvé: " + existingCollaborateur);

            // Vérifier si l'utilisateur est bien celui qui a créé le collaborateur
            if (!existingCollaborateur.getUser().getId().equals(user.getId())) {
                System.out.println("L'utilisateur n'est pas le créateur du collaborateur. ID utilisateur: " + user.getId() + ", ID créateur collaborateur: " + existingCollaborateur.getUser().getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'êtes pas autorisé à mettre à jour ce collaborateur");
            }

            // Mise à jour des informations du collaborateur
            existingCollaborateur.setNom(collaborateur.getNom());
            existingCollaborateur.setPrenom(collaborateur.getPrenom());
            existingCollaborateur.setDateEmbauche(collaborateur.getDateEmbauche());
            existingCollaborateur.setSeg(collaborateur.getSeg());

            // Sauvegarder les modifications
            collaborateurRepository.save(existingCollaborateur);

            return ResponseEntity.ok("Collaborateur mis à jour avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur pendant la mise à jour: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }

    
  /*  @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCollaborateur(@PathVariable Integer id, 
                                                 @RequestHeader("Authorization") String token) {
        System.out.println("Début de la suppression du collaborateur avec ID: " + id);
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println("Token est manquant ou malformé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token est manquant ou malformé");
            }

            String jwtToken = token.substring(7); // Retirer "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken);
            System.out.println("Claims du token: " + claims);

            // Vérification de l'email
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                System.out.println("L'email est manquant dans le token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email est manquant dans le token");
            }

            System.out.println("Email extrait du token: " + email);

            // Vérification de l'expiration du token
            Date expirationDate = claims.getExpiration();
            System.out.println("Date d'expiration du token: " + expirationDate);
            if (expirationDate != null && expirationDate.before(new Date())) {
                System.out.println("Le token a expiré");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expiré");
            }

            // Vérification du rôle
            String role = (String) claims.get("role");
            System.out.println("Rôle extrait du token: " + role);
            if (!"ROLE_SL".equals(role)) {
                System.out.println("L'utilisateur n'a pas le rôle requis pour la suppression");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas le rôle requis pour effectuer cette action");
            }

            // Rechercher l'utilisateur par email
            Optional<User> userOptional = Optional.of(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                System.out.println("Utilisateur non trouvé pour l'email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur avec cet email non trouvé");
            }

            User user = userOptional.get();
            System.out.println("Utilisateur trouvé: " + user);

            // Rechercher le collaborateur
            Optional<Collaborateur> collaborateurOptional = collaborateurRepository.findById(id);
            if (!collaborateurOptional.isPresent()) {
                System.out.println("Collaborateur non trouvé pour l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collaborateur non trouvé");
            }

            Collaborateur collaborateur = collaborateurOptional.get();
            System.out.println("Collaborateur trouvé: " + collaborateur);

            // Vérifier si l'utilisateur est bien celui qui a créé le collaborateur
            if (!collaborateur.getUser().getId().equals(user.getId())) {
                System.out.println("L'utilisateur n'est pas le créateur du collaborateur. ID utilisateur: " + user.getId() + ", ID créateur collaborateur: " + collaborateur.getUser().getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'êtes pas autorisé à supprimer ce collaborateur");
            }

            // Vérifier si le collaborateur a des entretiens associés
            List<EntretienExplicatif> entretiens = entretienExplicatifRepository.findByCollaborateurId(id);
            if (!entretiens.isEmpty()) {
                System.out.println("Le collaborateur a des entretiens associés, suppression impossible");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Le collaborateur a des entretiens associés, suppression impossible");
            }

            // Suppression du collaborateur
            collaborateurRepository.delete(collaborateur);
            System.out.println("Collaborateur supprimé avec succès");

            return ResponseEntity.ok("Collaborateur supprimé avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur pendant la suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }*/
    
    
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCollaborateur(@PathVariable Integer id, 
                                                 @RequestHeader("Authorization") String token) {
        System.out.println("Début de la suppression du collaborateur avec ID: " + id);
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println("Token est manquant ou malformé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token est manquant ou malformé");
            }

            String jwtToken = token.substring(7); // Retirer "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken);
            System.out.println("Claims du token: " + claims);

            // Vérification de l'email
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                System.out.println("L'email est manquant dans le token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email est manquant dans le token");
            }

            System.out.println("Email extrait du token: " + email);

            // Vérification de l'expiration du token
            Date expirationDate = claims.getExpiration();
            System.out.println("Date d'expiration du token: " + expirationDate);
            if (expirationDate != null && expirationDate.before(new Date())) {
                System.out.println("Le token a expiré");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expiré");
            }

            // Vérification du rôle
            String role = (String) claims.get("role");
            System.out.println("Rôle extrait du token: " + role);
            if (!"ROLE_SL".equals(role)) {
                System.out.println("L'utilisateur n'a pas le rôle requis pour la suppression");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas le rôle requis pour effectuer cette action");
            }

            // Rechercher l'utilisateur par email
            Optional<User> userOptional = Optional.of(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                System.out.println("Utilisateur non trouvé pour l'email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur avec cet email non trouvé");
            }

            User user = userOptional.get();
            System.out.println("Utilisateur trouvé: " + user);

            // Rechercher le collaborateur
            Optional<Collaborateur> collaborateurOptional = collaborateurRepository.findById(id);
            if (!collaborateurOptional.isPresent()) {
                System.out.println("Collaborateur non trouvé pour l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collaborateur non trouvé");
            }

            Collaborateur collaborateur = collaborateurOptional.get();
            System.out.println("Collaborateur trouvé: " + collaborateur);

            // Vérifier si l'utilisateur est bien celui qui a créé le collaborateur
            if (!collaborateur.getUser().getId().equals(user.getId())) {
                System.out.println("L'utilisateur n'est pas le créateur du collaborateur. ID utilisateur: " + user.getId() + ", ID créateur collaborateur: " + collaborateur.getUser().getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'êtes pas autorisé à supprimer ce collaborateur");
            }

            // Vérifier si le collaborateur a des entretiens associés
            List<EntretienExplicatif> entretiens = entretienExplicatifRepository.findByCollaborateurId(id);
            if (!entretiens.isEmpty()) {
                System.out.println("Le collaborateur a des entretiens associés, suppression impossible");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Le collaborateur a des entretiens associés, suppression impossible");
            }

            // Marquer le collaborateur comme supprimé (soft delete)
            collaborateur.setDeleted(true);
            collaborateurRepository.save(collaborateur); // Sauvegarder les modifications
            System.out.println("Collaborateur marqué comme supprimé et déplacé dans la corbeille");

            return ResponseEntity.ok("Collaborateur marqué comme supprimé et déplacé dans la corbeille");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur pendant la suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    @GetMapping("/restored")
    public ResponseEntity<?> getRestoredCollaborateurs(@RequestHeader("Authorization") String token) {
        System.out.println("Début de la récupération des collaborateurs restaurés");
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println("Token est manquant ou malformé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token est manquant ou malformé");
            }

            String jwtToken = token.substring(7); // Retirer "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken);
            System.out.println("Claims du token: " + claims);

            // Vérification de l'email
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                System.out.println("L'email est manquant dans le token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email est manquant dans le token");
            }

            System.out.println("Email extrait du token: " + email);

            // Vérification de l'expiration du token
            Date expirationDate = claims.getExpiration();
            System.out.println("Date d'expiration du token: " + expirationDate);
            if (expirationDate != null && expirationDate.before(new Date())) {
                System.out.println("Le token a expiré");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expiré");
            }

            // Vérification du rôle
         /*   String role = (String) claims.get("role");
            System.out.println("Rôle extrait du token: " + role);
            if (!"ROLE_SL".equals(role)) { // Remplacez "ROLE_SL" par le rôle requis
                System.out.println("L'utilisateur n'a pas le rôle requis pour accéder à cette ressource");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas le rôle requis pour accéder à cette ressource");
            }*/

            // Récupérer tous les collaborateurs restaurés (deleted = false)
            List<Collaborateur> restoredCollaborateurs = collaborateurRepository.findByDeletedFalse();
            System.out.println("Nombre de collaborateurs restaurés trouvés : " + restoredCollaborateurs.size());

            return ResponseEntity.ok(restoredCollaborateurs);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur pendant la récupération des collaborateurs restaurés : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    
    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedCollaborateurs(@RequestHeader("Authorization") String token) {
        System.out.println("Début de la récupération des collaborateurs supprimés");
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println("Token est manquant ou malformé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token est manquant ou malformé");
            }

            String jwtToken = token.substring(7); // Retirer "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken);
            System.out.println("Claims du token: " + claims);

            // Vérification de l'email
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                System.out.println("L'email est manquant dans le token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email est manquant dans le token");
            }

            System.out.println("Email extrait du token: " + email);

            // Vérification de l'expiration du token
            Date expirationDate = claims.getExpiration();
            System.out.println("Date d'expiration du token: " + expirationDate);
            if (expirationDate != null && expirationDate.before(new Date())) {
                System.out.println("Le token a expiré");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expiré");
            }

            // Vérification du rôle
           /* String role = (String) claims.get("role");
            System.out.println("Rôle extrait du token: " + role);
            if (!"ROLE_SL".equals(role)) { // Remplacez "ROLE_SL" par le rôle requis
                System.out.println("L'utilisateur n'a pas le rôle requis pour accéder à cette ressource");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas le rôle requis pour accéder à cette ressource");
            }*/

            // Récupérer tous les collaborateurs supprimés (deleted = true)
            List<Collaborateur> deletedCollaborateurs = collaborateurRepository.findByDeletedTrue();
            System.out.println("Nombre de collaborateurs supprimés trouvés : " + deletedCollaborateurs.size());

            return ResponseEntity.ok(deletedCollaborateurs);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur pendant la récupération des collaborateurs supprimés : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    
  /*  @GetMapping
    public ResponseEntity<List<CollaborateurDTO>> getAllCollaborateurs(@RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7); // Retirer le préfixe "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken); // Extraire les claims du token

            // Vérification de l'email
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer tous les collaborateurs avec l'utilisateur
            List<Collaborateur> collaborateurs = collaborateurRepository.findAll(); // Assurez-vous que la relation est chargée

            // Mapper les collaborateurs à des DTOs
            List<CollaborateurDTO> dtos = collaborateurs.stream()
                .map(c -> new CollaborateurDTO(
                    c.getId(),
                    c.getNom(),
                    c.getPrenom(),
                    c.getDateEmbauche(),
                    c.getSeg(),
                    c.getUser() != null ? c.getUser().getId() : null // Récupérer l'ID de l'utilisateur
                ))
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }*/

 /*   
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Collaborateur>> getCollaborateursByUserId(
            @PathVariable Long userId, 
            @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7); // Retirer le préfixe "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken); // Extraire les claims du token

            // Vérification de l'email
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Vérification de l'utilisateur
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer les collaborateurs par ID d'utilisateur
            List<Collaborateur> collaborateurs = collaborateurRepository.findByUserId(userId);
            return ResponseEntity.ok(collaborateurs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    */
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Collaborateur>> getCollaborateursByUserId(
            @PathVariable Long userId, 
            @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7); // Retirer le préfixe "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken); // Extraire les claims du token

            // Vérification de l'email
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Vérification de l'utilisateur
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer les collaborateurs non supprimés par ID d'utilisateur
            List<Collaborateur> collaborateurs = collaborateurRepository.findByUserIdAndDeletedFalse(userId);
            return ResponseEntity.ok(collaborateurs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Collaborateur>> searchCollaborateurs(
            @RequestParam String nom, 
            @RequestParam String prenom, 
            @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7); // Retirer le préfixe "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken); // Extraire les claims du token

            // Vérification de l'email
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Vérification de l'utilisateur
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer les collaborateurs par nom et prénom
            List<Collaborateur> collaborateurs = collaborateurRepository.findByNomAndPrenom(nom, prenom);
            return ResponseEntity.ok(collaborateurs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
    @GetMapping
    public ResponseEntity<List<CollaborateurDTO>> getAllCollaborateursNonSupprimes(@RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7);
            Claims claims = jwtUtils.extractAllClaims(jwtToken);
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer tous les collaborateurs non supprimés avec leurs utilisateurs
            List<Collaborateur> collaborateurs = collaborateurRepository.findByDeletedFalse();

            // Mapper vers DTO avec les informations complètes de l'utilisateur
            List<CollaborateurDTO> dtos = collaborateurs.stream()
                .map(c -> {
                    User user = c.getUser();
                    UserDTO userDTO = null;
                    
                    if (user != null) {
                        userDTO = new UserDTO();
                        userDTO.setId(user.getId());
                        userDTO.setFirstName(user.getFirstName());
                        userDTO.setLastName(user.getLastName());
                        userDTO.setEmail(user.getEmail());
                        userDTO.setRole(user.getRole());
                        userDTO.setPlant(user.getPlant());
                        userDTO.setCreatedBy(user.getCreatedBy());
                        userDTO.setSegment(user.getSegment());
                    }
                    
                    return new CollaborateurDTO(
                        c.getId(),
                        c.getNom(),
                        c.getPrenom(),
                        c.getDateEmbauche(),
                        c.getSeg(),
                        userDTO
                    );
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
    
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> checkCollaborateurExists(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }

            String jwtToken = token.substring(7);
            Claims claims = jwtUtils.extractAllClaims(jwtToken);
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }

            // Vérification que l'utilisateur existe
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }

            // Vérification de l'existence du collaborateur
            Optional<Collaborateur> collaborateurOpt = collaborateurRepository.findById(id);
            if (!collaborateurOpt.isPresent() || collaborateurOpt.get().isDeleted()) {
                return ResponseEntity.ok(false);
            }

            // Si l'utilisateur est SL, vérifier qu'il est le créateur
            if ("SL".equals(user.getRole())) {
                boolean isCreator = collaborateurOpt.get().getUser().getId().equals(user.getId());
                return ResponseEntity.ok(isCreator);
            }

            // Pour les non-SL, retourner simplement si le collaborateur existe
            return ResponseEntity.ok(true);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
    
    @PostMapping("/verify-sgl-creation")
    public ResponseEntity<VerificationResponse> verifySglCreation(
            @RequestBody Map<String, Object> requestMap) {
        
        Long sglId = Long.parseLong(requestMap.get("sglId").toString());
        Integer collaborateurId = Integer.parseInt(requestMap.get("collaborateurId").toString());
        
        VerificationResponse response = collaborateurService.verifySglCreationChain(
            sglId, 
            collaborateurId
        );
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/update-owner/{collaborateurId}")
    @Transactional
    public ResponseEntity<?> updateCollaborateurOwner(
            @PathVariable Integer collaborateurId,
            @RequestBody Map<String, Long> requestBody,
            @RequestHeader("Authorization") String token) {

        Long newUserId = requestBody.get("newUserId");
        if (newUserId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("newUserId is required");
        }

        return collaborateurService.updateCollaborateurUserId(collaborateurId, newUserId, token);
    }
    
    
    @GetMapping("/corbeille/user/{userId}")
    public ResponseEntity<List<CollaborateurDTO>> getCorbeilleCollaborateursByUserId(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7);
            Claims claims = jwtUtils.extractAllClaims(jwtToken);
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer les collaborateurs supprimés par ID d'utilisateur
            List<Collaborateur> collaborateurs = collaborateurRepository.findByUserIdAndDeletedTrue(userId);
            
            // Mapper vers DTO avec les informations du créateur
            List<CollaborateurDTO> collaborateurDTOs = collaborateurs.stream()
                    .map(collaborateur -> {
                        User createur = collaborateur.getUser();
                        UserDTO userDTO = new UserDTO();
                        userDTO.setId(createur.getId());
                        userDTO.setFirstName(createur.getFirstName());
                        userDTO.setLastName(createur.getLastName());
                        // Vous pouvez ajouter d'autres champs si nécessaire
                        
                        return new CollaborateurDTO(
                            collaborateur.getId(),
                            collaborateur.getNom(),
                            collaborateur.getPrenom(),
                            collaborateur.getDateEmbauche(),
                            collaborateur.getSeg(),
                            userDTO
                        );
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(collaborateurDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/corbeille/plant/{plant}")
    public ResponseEntity<List<CollaborateurDTO>> getCorbeilleCollaborateursByPlant(
            @PathVariable String plant,
            @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.substring(7);
            Claims claims = jwtUtils.extractAllClaims(jwtToken);
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Récupérer les collaborateurs supprimés par plant
            List<Collaborateur> collaborateurs = collaborateurRepository.findByPlantAndDeletedTrue(plant);
            
            // Mapper vers DTO avec les informations du créateur
            List<CollaborateurDTO> collaborateurDTOs = collaborateurs.stream()
                    .map(collaborateur -> {
                        User createur = collaborateur.getUser();
                        UserDTO userDTO = new UserDTO();
                        userDTO.setId(createur.getId());
                        userDTO.setFirstName(createur.getFirstName());
                        userDTO.setLastName(createur.getLastName());
                        // Vous pouvez ajouter d'autres champs si nécessaire
                        
                        return new CollaborateurDTO(
                            collaborateur.getId(),
                            collaborateur.getNom(),
                            collaborateur.getPrenom(),
                            collaborateur.getDateEmbauche(),
                            collaborateur.getSeg(),
                            userDTO
                        );
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(collaborateurDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/creator-info/{collaborateurId}")
    public ResponseEntity<?> getCollaborateurCreatorInfo(
            @PathVariable Integer collaborateurId,
            @RequestHeader("Authorization") String token) {
        try {
            // Vérification du token (similaire à vos autres méthodes)
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide");
            }

            // Extraire et valider le token si nécessaire...

            // Appeler le service
            CollaborateurCreatorInfoDTO info = collaborateurService.getCollaborateurCreatorInfo(collaborateurId);
            return ResponseEntity.ok(info);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    
    @PutMapping("/update-collaborateur-and-user")
    public ResponseEntity<?> updateCollaborateurAndUser(
            @RequestBody Map<String, Object> requestBody) {
        Integer collaborateurId = ((Number) requestBody.get("collaborateurId")).intValue();
        Long newUserId = ((Number) requestBody.get("newUserId")).longValue();
        Long newCreatedBy = ((Number) requestBody.get("newCreatedBy")).longValue();

        return collaborateurService.updateCollaborateurAndUser(collaborateurId, newUserId, newCreatedBy);
    }
 
}