package projetPAQ.PAQBackend.DTO;

import java.sql.Date;
//import java.time.LocalDate;
import lombok.Data;
@Data
public class EntretienDaccordDTO {
    private Integer id;
    private Date date;
    private String typeErreur;
    private String details;
    private String decision;
 // Collaborateur info
    private Integer collaborateurId;
    private String collaborateurNom;
    private String collaborateurPrenom;
    
    // User info
    private Long userId;
    private String userNom;    // Nouveau champ
    private String userPrenom; // Nouveau champ
    private Long validatedByUserId; // ID du validateur
    private String validatedByFirstName; // Prénom du validateur
    private String validatedByLastName; // Nom du validateur
    private Date validationDate; // Date de validation
    private Date dateArchivage;
 

   
}