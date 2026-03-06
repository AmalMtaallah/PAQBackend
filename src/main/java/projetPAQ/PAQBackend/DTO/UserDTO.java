package projetPAQ.PAQBackend.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String plant;       // Nouveau champ
    private Long createdBy;
    private String segment;
    // Getters and Setters
    
    
    
}