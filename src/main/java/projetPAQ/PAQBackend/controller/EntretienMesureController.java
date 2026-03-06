package projetPAQ.PAQBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetPAQ.PAQBackend.DTO.EntretienDeMesureDTO;
import projetPAQ.PAQBackend.entity.EntretienDeMesure;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.service.EntretienDeMesureService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entretiensDeMesure")
public class EntretienMesureController {

    private final EntretienDeMesureService entretienDeMesureService;

    @Autowired
    public EntretienMesureController(EntretienDeMesureService entretienDeMesureService) {
        this.entretienDeMesureService = entretienDeMesureService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEntretien(@RequestBody EntretienDeMesureDTO entretienDTO) {
        try {
            // Créer une nouvelle entité EntretienDeMesure
            EntretienDeMesure entretien = new EntretienDeMesure();
            entretien.setDate(entretienDTO.getDate());
            entretien.setTypeErreur(entretienDTO.getTypeErreur());
            entretien.setDetails(entretienDTO.getDetails());
            entretien.setDecision(entretienDTO.getDecision());

            // Appeler le service pour créer l'entretien
            EntretienDeMesure savedEntretien = entretienDeMesureService.createEntretien(
                entretien,
                entretienDTO.getCollaborateurId(),
                entretienDTO.getUserId()
            );

            // Convertir en DTO et retourner la réponse
            EntretienDeMesureDTO dto = entretienDeMesureService.convertToDTO(savedEntretien);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEntretien(@PathVariable Integer id, @RequestBody EntretienDeMesureDTO entretienDTO) {
        try {
            EntretienDeMesure updatedEntretienResult = entretienDeMesureService.updateEntretien(id, entretienDTO);
            EntretienDeMesureDTO dto = entretienDeMesureService.convertToDTO(updatedEntretienResult);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEntretien(@PathVariable Integer id) {
        try {
            entretienDeMesureService.deleteEntretien(id);
            return ResponseEntity.ok("Entretien marqué comme supprimé avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/validate/qmseegmet/{id}")
    public ResponseEntity<?> validateByQMseegmet(@PathVariable Integer id, @RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email"); // Extraire l'email du corps JSON
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("L'email est requis dans le corps de la requête.");
            }

            EntretienDeMesure validatedEntretien = entretienDeMesureService.validateByQMseegmet(id, email);
            EntretienDeMesureDTO dto = entretienDeMesureService.convertToDTO(validatedEntretien);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/validate/sgl/{id}")
    public ResponseEntity<?> validateBySGL(@PathVariable Integer id, @RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email"); // Extraire l'email du corps JSON
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("L'email est requis dans le corps de la requête.");
            }

            EntretienDeMesure validatedEntretien = entretienDeMesureService.validateBySGL(id, email);
            EntretienDeMesureDTO dto = entretienDeMesureService.convertToDTO(validatedEntretien);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getEntretienDetails(@PathVariable Integer id) {
        try {
            Optional<EntretienDeMesure> entretienOpt = entretienDeMesureService.getEntretienById(id);
            if (entretienOpt.isPresent()) {
                EntretienDeMesureDTO dto = entretienDeMesureService.convertToDTO(entretienOpt.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun entretien de mesure trouvé avec cet ID.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    
    @GetMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<?> getEntretienByCollaborateurId(@PathVariable Integer collaborateurId) {
        try {
            Optional<EntretienDeMesure> entretienOpt = entretienDeMesureService.getEntretienByCollaborateurId(collaborateurId);
            if (entretienOpt.isPresent()) {
                EntretienDeMesureDTO dto = entretienDeMesureService.convertToDTO(entretienOpt.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun entretien de mesure trouvé pour ce collaborateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/archived")
    public ResponseEntity<List<EntretienDeMesureDTO>> getArchivedAndNotDeletedEntretiens() {
        try {
            // Récupérer la liste des entretiens archivés et non supprimés
            List<EntretienDeMesure> entretiens = entretienDeMesureService.getArchivedAndNotDeletedEntretiens();

            // Convertir la liste en DTOs
            List<EntretienDeMesureDTO> entretienDTOs = entretiens.stream()
                .map(entretienDeMesureService::convertToDTO)
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
            List<EntretienDeMesure> deletedNonArchivedEntretiens = entretienDeMesureService.getDeletedNonArchivedEntretiens();
            List<EntretienDeMesureDTO> dtos = deletedNonArchivedEntretiens.stream()
                    .map(entretienDeMesureService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    
}