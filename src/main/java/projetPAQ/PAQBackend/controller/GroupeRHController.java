package projetPAQ.PAQBackend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projetPAQ.PAQBackend.DTO.UserDTO;
import projetPAQ.PAQBackend.entity.GroupeRH;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.service.GroupeRHService;

@RestController
@RequestMapping("/api/groupesRH")
public class GroupeRHController {
    private final GroupeRHService groupeRHService;
    @Autowired
    public GroupeRHController(GroupeRHService groupeRHService) {
        this.groupeRHService = groupeRHService;
    }
    // ... autres endpoints ...

    @GetMapping("/{id}/membres")
    public ResponseEntity<List<UserDTO>> getMembres(@PathVariable Long id) {
        try {
            List<UserDTO> membres = groupeRHService.getMembresWithDetails(id);
            return ResponseEntity.ok(membres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/byPlant/{plant}")
    public ResponseEntity<List<GroupeRH>> getGroupesByPlant(@PathVariable String plant) {
        try {
            List<GroupeRH> groupes = groupeRHService.getGroupesByPlant(plant);
            return ResponseEntity.ok(groupes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/addMembre")
    public ResponseEntity<GroupeRH> addMembre(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String userEmail = request.get("userEmail");
        GroupeRH groupe = groupeRHService.addMembreToGroupeByEmail(id, userEmail);
        return ResponseEntity.ok(groupe);
    }

    @PostMapping("/{id}/removeMembre")
    public ResponseEntity<GroupeRH> removeMembre(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        GroupeRH groupe = groupeRHService.removeMembreFromGroupe(id, userId);
        return ResponseEntity.ok(groupe);
    }
    
    
    @PostMapping
    public ResponseEntity<GroupeRH> createGroupeRH(@RequestBody GroupeRH groupeRH) {
        try {
            GroupeRH nouveauGroupe = groupeRHService.createGroupeRH(groupeRH);
            return new ResponseEntity<>(nouveauGroupe, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroupeRH(@PathVariable Long id) {
        try {
            groupeRHService.deleteGroupeRH(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Error
        }
    }
}