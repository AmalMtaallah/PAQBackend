package projetPAQ.PAQBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetPAQ.PAQBackend.DTO.EntretienDaccordDTO;
import projetPAQ.PAQBackend.entity.EntretienDaccord;
import projetPAQ.PAQBackend.service.EntretienDaccordService;
import projetPAQ.PAQBackend.configuration.JwtUtils;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.UserRepository;

import java.util.List;
//import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate; 
@RestController
@RequestMapping("/api/entretiensDaccord")
public class EntretienDaccordController {

    private final EntretienDaccordService entretienDaccordService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Autowired
    public EntretienDaccordController(EntretienDaccordService entretienDaccordService, JwtUtils jwtUtils, UserRepository userRepository) {
        this.entretienDaccordService = entretienDaccordService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEntretien(@RequestBody EntretienDaccord entretien, @RequestHeader("Authorization") String token) {
        try {
            String userRole = validateTokenAndGetRole(token);
            EntretienDaccord savedEntretien = entretienDaccordService.createEntretien(entretien, userRole);
            EntretienDaccordDTO dto = entretienDaccordService.convertToDTO(savedEntretien);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEntretien(
            @PathVariable Integer id,
            @RequestBody EntretienDaccord updatedEntretien,
            @RequestHeader("Authorization") String token) {
        try {
            // Valider le token et récupérer le rôle de l'utilisateur
            String userRole = validateTokenAndGetRole(token);

            // Appeler le service pour mettre à jour l'entretien
            EntretienDaccord updatedEntretienResult = entretienDaccordService.updateEntretien(id, updatedEntretien, userRole);

            // Convertir en DTO et retourner la réponse
            EntretienDaccordDTO dto = entretienDaccordService.convertToDTO(updatedEntretienResult);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getEntretienDetails(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        try {
            // Valider le token (sans vérification de rôle)
            validateToken(token);

            // Récupérer les détails de l'entretien
            Optional<EntretienDaccord> entretienOpt = entretienDaccordService.getEntretienDetailsById(id);

            if (entretienOpt.isPresent()) {
                // Convertir l'entretien en DTO
                EntretienDaccordDTO dto = entretienDaccordService.convertToDTO(entretienOpt.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun entretien d'accord trouvé avec cet ID.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEntretien(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        try {
            String userRole = validateTokenAndGetRole(token);
            entretienDaccordService.deleteEntretien(id, userRole);
            return ResponseEntity.ok("Entretien marqué comme supprimé avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
   /* @GetMapping("/corbeille")
    public ResponseEntity<?> getDeletedEntretiens(@RequestHeader("Authorization") String token) {
        try {
            validateToken(token); // Valider le token
            List<EntretienDaccord> deletedEntretiens = entretienDaccordService.getDeletedEntretiens();
            List<EntretienDaccordDTO> dtos = deletedEntretiens.stream()
                    .map(entretienDaccordService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }*/
    @GetMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<?> getEntretiensByCollaborateurId(@PathVariable Integer collaborateurId, 
                                                          @RequestHeader("Authorization") String token) {
        try {
            validateToken(token);
            List<EntretienDaccord> entretiens = entretienDaccordService.getEntretiensByCollaborateurId(collaborateurId);

            if (!entretiens.isEmpty()) {
                List<EntretienDaccordDTO> dtos = entretiens.stream()
                        .map(entretienDaccordService::convertToDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                       .body("Aucun entretien d'accord trouvé pour ce collaborateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @PostMapping("/validate/{id}")
    public ResponseEntity<?> validateEntretien(
            @PathVariable Integer id, 
            @RequestHeader("Authorization") String token) {
        try {
            // Valider le token et récupérer l'utilisateur
            String jwtToken = token.substring(7);
            String email = jwtUtils.extractAllClaims(jwtToken).getSubject();
            User validator = userRepository.findByEmail(email);

            // Appeler le service pour valider l'entretien
            EntretienDaccord validatedEntretien = entretienDaccordService.validateEntretien(
                id, 
                validator.getRole(), 
                validator
            );

            // Convertir en DTO et retourner la réponse
            EntretienDaccordDTO dto = entretienDaccordService.convertToDTO(validatedEntretien);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    
    
    
    
    
    
    
    

    private void validateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token is missing or malformed");
        }

        String jwtToken = token.substring(7);
        String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is missing in the token");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with the given email not found");
        }
    }
    private String validateTokenAndGetRole(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token is missing or malformed");
        }

        String jwtToken = token.substring(7);
        String email = jwtUtils.extractAllClaims(jwtToken).getSubject();

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is missing in the token");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with the given email not found");
        }

        return user.getRole();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getEntretiensByUserId(@PathVariable Integer userId, @RequestHeader("Authorization") String token) {
        try {
            // Valider le token (sans vérification de rôle)
            validateToken(token);

            // Récupérer les entretiens par userId
            List<EntretienDaccord> entretiens = entretienDaccordService.getEntretiensByUserId(userId);

            if (!entretiens.isEmpty()) {
                // Convertir les entretiens en DTOs
                List<EntretienDaccordDTO> dtos = entretiens.stream()
                        .map(entretienDaccordService::convertToDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun entretien d'accord trouvé pour cet utilisateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    @GetMapping("/deleted/user/{userId}")
    public ResponseEntity<?> getDeletedEntretiensByUserId(@PathVariable Integer userId, @RequestHeader("Authorization") String token) {
        try {
            // Valider le token (sans vérification de rôle)
            validateToken(token);

            // Récupérer les entretiens supprimés par userId
            List<EntretienDaccord> deletedEntretiens = entretienDaccordService.getDeletedEntretiensByUserId(userId);

            if (!deletedEntretiens.isEmpty()) {
                // Convertir les entretiens en DTOs
                List<EntretienDaccordDTO> dtos = deletedEntretiens.stream()
                        .map(entretienDaccordService::convertToDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun entretien d'accord supprimé trouvé pour cet utilisateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    
    
    
    
    
    
    
    
    @GetMapping("/archives")
    public ResponseEntity<?> getArchivedEntretiens(@RequestHeader("Authorization") String token) {
        try {
            validateToken(token); // Valider le token
            
            List<EntretienDaccord> archivedEntretiens = entretienDaccordService.getArchivedEntretiens();
            
            // Convertir en DTOs
            List<EntretienDaccordDTO> dtos = archivedEntretiens.stream()
                    .map(entretienDaccordService::convertToDTO)
                    .collect(Collectors.toList());
                    
            return ResponseEntity.ok(dtos);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    
    @GetMapping("/corbeille")
    public ResponseEntity<?> getDeletedNonArchivedEntretiens(@RequestHeader("Authorization") String token) {
        try {
            validateToken(token); // Valider le token
            List<EntretienDaccord> deletedNonArchivedEntretiens = entretienDaccordService.getDeletedNonArchivedEntretiens();
            List<EntretienDaccordDTO> dtos = deletedNonArchivedEntretiens.stream()
                    .map(entretienDaccordService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}