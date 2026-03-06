package projetPAQ.PAQBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetPAQ.PAQBackend.DTO.PhaseDialoguePositifDTO;
import projetPAQ.PAQBackend.entity.PhaseDialoguePositif;
import projetPAQ.PAQBackend.service.PhaseDialoguePositifService;
import projetPAQ.PAQBackend.configuration.JwtUtils;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/phasesDialoguePositif")
public class PhaseDialoguePositifController {

    private final PhaseDialoguePositifService phaseDialoguePositifService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Autowired
    public PhaseDialoguePositifController(PhaseDialoguePositifService phaseDialoguePositifService, 
                                        JwtUtils jwtUtils, 
                                        UserRepository userRepository) {
        this.phaseDialoguePositifService = phaseDialoguePositifService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPhase(@RequestBody PhaseDialoguePositifDTO phaseDTO, 
                                     @RequestHeader("Authorization") String token) {
        try {
            String userRole = validateTokenAndGetRole(token);
            
            // Convertir le DTO en entité
            PhaseDialoguePositif phase = phaseDialoguePositifService.convertToEntity(phaseDTO);
            
            PhaseDialoguePositif savedPhase = phaseDialoguePositifService.createPhase(phase, userRole);
            PhaseDialoguePositifDTO dto = phaseDialoguePositifService.convertToDTO(savedPhase);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePhase(
            @PathVariable Integer id,
            @RequestBody PhaseDialoguePositif updatedPhase,
            @RequestHeader("Authorization") String token) {
        try {
            String userRole = validateTokenAndGetRole(token);
            PhaseDialoguePositif updatedPhaseResult = phaseDialoguePositifService.updatePhase(id, updatedPhase, userRole);
            PhaseDialoguePositifDTO dto = phaseDialoguePositifService.convertToDTO(updatedPhaseResult);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getPhaseDetails(@PathVariable Integer id, 
                                           @RequestHeader("Authorization") String token) {
        try {
            validateToken(token);
            Optional<PhaseDialoguePositif> phaseOpt = phaseDialoguePositifService.getPhaseDetailsById(id);

            if (phaseOpt.isPresent()) {
                PhaseDialoguePositifDTO dto = phaseDialoguePositifService.convertToDTO(phaseOpt.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune phase de dialogue positif trouvée avec cet ID.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePhase(@PathVariable Integer id, 
                                       @RequestHeader("Authorization") String token) {
        try {
            String userRole = validateTokenAndGetRole(token);
            phaseDialoguePositifService.deletePhase(id, userRole);
            return ResponseEntity.ok("Phase marquée comme supprimée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/corbeille")
    public ResponseEntity<?> getDeletedPhases(@RequestHeader("Authorization") String token) {
        try {
            validateToken(token);
            List<PhaseDialoguePositif> deletedPhases = phaseDialoguePositifService.getDeletedPhases();
            List<PhaseDialoguePositifDTO> dtos = deletedPhases.stream()
                    .map(phaseDialoguePositifService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<?> getPhasesByCollaborateurId(@PathVariable Integer collaborateurId, 
                                                       @RequestHeader("Authorization") String token) {
        try {
            validateToken(token);
            List<PhaseDialoguePositif> phases = phaseDialoguePositifService.getPhasesByCollaborateurId(collaborateurId);

            if (!phases.isEmpty()) {
                List<PhaseDialoguePositifDTO> dtos = phases.stream()
                        .map(phaseDialoguePositifService::convertToDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune phase de dialogue positif trouvée pour ce collaborateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPhasesByUserId(@PathVariable Integer userId, 
                                             @RequestHeader("Authorization") String token) {
        try {
            validateToken(token);
            List<PhaseDialoguePositif> phases = phaseDialoguePositifService.getPhasesByUserId(userId);

            if (!phases.isEmpty()) {
                List<PhaseDialoguePositifDTO> dtos = phases.stream()
                        .map(phaseDialoguePositifService::convertToDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune phase de dialogue positif trouvée pour cet utilisateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/deleted/user/{userId}")
    public ResponseEntity<?> getDeletedPhasesByUserId(@PathVariable Integer userId, 
                                                    @RequestHeader("Authorization") String token) {
        try {
            validateToken(token);
            List<PhaseDialoguePositif> deletedPhases = phaseDialoguePositifService.getDeletedPhasesByUserId(userId);

            if (!deletedPhases.isEmpty()) {
                List<PhaseDialoguePositifDTO> dtos = deletedPhases.stream()
                        .map(phaseDialoguePositifService::convertToDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune phase de dialogue positif supprimée trouvée pour cet utilisateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
    
    @GetMapping("/archives")
    public ResponseEntity<?> getArchivedPhases(@RequestHeader("Authorization") String token) {
        try {
            validateToken(token);
            List<PhaseDialoguePositif> archivedPhases = phaseDialoguePositifService.getArchivedPhases();
            List<PhaseDialoguePositifDTO> dtos = archivedPhases.stream()
                    .map(phaseDialoguePositifService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    
    
}