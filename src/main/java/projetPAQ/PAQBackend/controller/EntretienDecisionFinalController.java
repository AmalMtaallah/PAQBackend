package projetPAQ.PAQBackend.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projetPAQ.PAQBackend.DTO.EntretienDecisionFinalDTO;
import projetPAQ.PAQBackend.entity.EntretienDecisionFinal;
import projetPAQ.PAQBackend.service.EntretienDecisionFinalService;

@RestController
@RequestMapping("/api/entretiensDecisionFinal")
public class EntretienDecisionFinalController {
    private final EntretienDecisionFinalService entretienService;
    @Autowired
    public EntretienDecisionFinalController(EntretienDecisionFinalService entretienService) {
        this.entretienService = entretienService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEntretien(@RequestBody EntretienDecisionFinalDTO entretienDTO) {
        try {
            EntretienDecisionFinal entretien = new EntretienDecisionFinal();
            // Set des champs de base
            entretien.setDate(entretienDTO.getDate());
            entretien.setTypeErreur(entretienDTO.getTypeErreur());
            entretien.setDetails(entretienDTO.getDetails());
            entretien.setDecisionFinale(entretienDTO.getDecisionFinale());

            EntretienDecisionFinal savedEntretien = entretienService.createEntretien(
                entretien,
                entretienDTO.getCollaborateurId(),
                entretienDTO.getUserId(),
                entretienDTO.getGroupeRHId()
            );

            EntretienDecisionFinalDTO dto = entretienService.convertToDTO(savedEntretien);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/validate/{id}")
    public ResponseEntity<?> validateEntretien(@PathVariable Integer id, @RequestBody Map<String, Long> request) {
        try {
            Long validateurId = request.get("validateurId");
            if (validateurId == null) {
                return ResponseEntity.badRequest().body("ID du validateur requis");
            }

            EntretienDecisionFinal validatedEntretien = entretienService.validateByRH(id, validateurId);
            return ResponseEntity.ok(entretienService.convertToDTO(validatedEntretien));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getEntretienById(@PathVariable Integer id) {
        try {
            EntretienDecisionFinal entretien = entretienService.getByIdAndNotDeleted(id);
            return ResponseEntity.ok(entretienService.convertToDTO(entretien));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<?> getEntretiensByCollaborateur(@PathVariable Integer collaborateurId) {
        try {
            List<EntretienDecisionFinalDTO> entretiens = entretienService.getByCollaborateurId(collaborateurId);
            return ResponseEntity.ok(entretiens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

   
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEntretien(
            @PathVariable Integer id,
            @RequestBody EntretienDecisionFinalDTO updateDTO) {
        try {
            EntretienDecisionFinal updated = entretienService.updateEntretienFields(id, updateDTO);
            return ResponseEntity.ok(entretienService.convertToDTO(updated));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEntretien(@PathVariable Integer id) {
        try {
            entretienService.softDeleteEntretien(id);
            return ResponseEntity.ok("Entretien marqué comme supprimé");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    
    @GetMapping("/archived")
    public ResponseEntity<List<EntretienDecisionFinalDTO>> getArchivedAndNotDeletedEntretiens() {
        try {
            // Récupérer la liste des entretiens archivés et non supprimés
            List<EntretienDecisionFinal> entretiens = entretienService.getArchivedAndNotDeletedEntretiens();

            // Convertir la liste en DTOs
            List<EntretienDecisionFinalDTO> entretienDTOs = entretiens.stream()
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
            List<EntretienDecisionFinal> deletedNonArchivedEntretiens = entretienService.getDeletedNonArchivedEntretiens();
            List<EntretienDecisionFinalDTO> dtos = deletedNonArchivedEntretiens.stream()
                    .map(entretienService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
