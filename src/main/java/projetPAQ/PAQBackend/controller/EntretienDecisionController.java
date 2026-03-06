package projetPAQ.PAQBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetPAQ.PAQBackend.DTO.EntretienDeDecisionDTO;
import projetPAQ.PAQBackend.entity.EntretienDeDecision;
import projetPAQ.PAQBackend.service.EntretienDeDecisionService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entretiensDecision")
public class EntretienDecisionController {

    private final EntretienDeDecisionService entretienService;

    @Autowired
    public EntretienDecisionController(EntretienDeDecisionService entretienService) {
        this.entretienService = entretienService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEntretien(@RequestBody EntretienDeDecisionDTO entretienDTO) {
        try {
            EntretienDeDecision entretien = new EntretienDeDecision();
            entretien.setDate(entretienDTO.getDate());
            entretien.setTypeErreur(entretienDTO.getTypeErreur());
            entretien.setDetails(entretienDTO.getDetails());
            entretien.setDecision(entretienDTO.getDecision());

            EntretienDeDecision savedEntretien = entretienService.createEntretien(
                entretien,
                entretienDTO.getCollaborateurId(),
                entretienDTO.getUserId()
            );

            EntretienDeDecisionDTO dto = entretienService.convertToDTO(savedEntretien);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEntretien(@PathVariable Integer id, @RequestBody EntretienDeDecisionDTO entretienDTO) {
        try {
            EntretienDeDecision updatedEntretien = entretienService.updateEntretien(id, entretienDTO);
            EntretienDeDecisionDTO dto = entretienService.convertToDTO(updatedEntretien);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEntretien(@PathVariable Integer id) {
        try {
            entretienService.deleteEntretien(id);
            return ResponseEntity.ok("Entretien de décision marqué comme supprimé");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/validate/hp/{id}")
    public ResponseEntity<?> validateByHP(@PathVariable Integer id, @RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email est requis");
            }

            EntretienDeDecision validatedEntretien = entretienService.validateByHP(id, email);
            EntretienDeDecisionDTO dto = entretienService.convertToDTO(validatedEntretien);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<EntretienDeDecisionDTO>> getAllEntretiens() {
        List<EntretienDeDecision> entretiens = entretienService.getAllEntretiens();
        List<EntretienDeDecisionDTO> dtos = entretiens.stream()
                .map(entretienService::convertToDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getEntretienDetails(@PathVariable Integer id) {
        try {
            Optional<EntretienDeDecision> entretienOpt = entretienService.getEntretienById(id);
            if (entretienOpt.isPresent()) {
                EntretienDeDecisionDTO dto = entretienService.convertToDTO(entretienOpt.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entretien non trouvé");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<?> getEntretiensByCollaborateur(@PathVariable Integer collaborateurId) {
        try {
            List<EntretienDeDecisionDTO> entretiens = entretienService.getEntretiensByCollaborateurId(collaborateurId);
            return ResponseEntity.ok(entretiens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des entretiens: " + e.getMessage());
        }
    }
    
    @PostMapping("/validate/qm/{id}")
    public ResponseEntity<?> validateByQM(@PathVariable Integer id, @RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email est requis");
            }

            EntretienDeDecision validatedEntretien = entretienService.validateByQM(id, email);
            EntretienDeDecisionDTO dto = entretienService.convertToDTO(validatedEntretien);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    
    

    @GetMapping("/archived")
    public ResponseEntity<List<EntretienDeDecisionDTO>> getArchivedAndNotDeletedEntretiens() {
        try {
            // Récupérer la liste des entretiens archivés et non supprimés
            List<EntretienDeDecision> entretiens = entretienService.getArchivedAndNotDeletedEntretiens();

            // Convertir la liste en DTOs
            List<EntretienDeDecisionDTO> entretienDTOs = entretiens.stream()
                .map(entretienService::convertToDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(entretienDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/corbeille")
    public ResponseEntity<?> getDeletedNonArchivedEntretiens() {
        try {
            List<EntretienDeDecision> deletedNonArchivedEntretiens = entretienService.getDeletedNonArchivedEntretiens();
            List<EntretienDeDecisionDTO> dtos = deletedNonArchivedEntretiens.stream()
                    .map(entretienService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}