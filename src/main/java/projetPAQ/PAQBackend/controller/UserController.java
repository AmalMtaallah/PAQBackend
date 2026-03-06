package projetPAQ.PAQBackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import projetPAQ.PAQBackend.DTO.SglWithSlDTO;
import projetPAQ.PAQBackend.DTO.UserDTO;
import projetPAQ.PAQBackend.service.CustomUserDetailsService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor // Si vous utilisez Lombok
public class UserController {
	

    private final CustomUserDetailsService userService; // Injection de UserService

    @GetMapping("/email/{userId}")
    public ResponseEntity<String> getUserEmail(@PathVariable Long userId) {
        String email = userService.getUserEmailById(userId);
        if (email != null) {
            return ResponseEntity.ok(email);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
    
    @GetMapping("/same-plant-qm-hp-rh/{userId}")
    public List<UserDTO> getQMPlantHPAndRHUsersFromSamePlant(@PathVariable Long userId) {
        return userService.getQMPlantHPAndRHUsersFromSamePlant(userId);
    }
    
    
    
    @GetMapping("/sgl-with-sl")
    public ResponseEntity<List<SglWithSlDTO>> getAllSglWithSlList() {
        List<SglWithSlDTO> sglWithSlList = userService.getAllSglWithSlList();
        return ResponseEntity.ok(sglWithSlList);
    }
    
    
  
}
