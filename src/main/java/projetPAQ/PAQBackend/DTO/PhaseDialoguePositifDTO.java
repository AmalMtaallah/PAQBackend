package projetPAQ.PAQBackend.DTO;

import java.sql.Date;

import lombok.Data;

@Data
public class PhaseDialoguePositifDTO {
    private Integer id;
    private Date date;
    private String commentaires;
    
    // Informations du collaborateur
    private Integer collaborateurId;
    private String collaborateurNom;
    private String collaborateurPrenom;
    
    // Informations de l'utilisateur (superviseur)
    private Long userId;
    private String userNom;
    private String userPrenom;
    private Date dateArchivage; 
}