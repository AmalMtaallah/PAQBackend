package projetPAQ.PAQBackend.DTO;

import java.sql.Date;
import lombok.Data;

@Data
public class EntretienDeDecisionDTO {
    private Integer id;
    private Date date;
    private String typeErreur;
    private String details;
    private String decision;
    
    private Integer collaborateurId;
    private String collaborateurNom;
    private String collaborateurPrenom;
    
    private Long userId;
    private String userNom;
    private String userPrenom;
    
    private Long validatedHPUserId;
    private String validatedHPNom;
    private String validatedHPPrenom;
    private Date HPValidationDate;
    
    private Long validatedQMUserId;
    private String validatedQMNom;
    private String validatedQMPrenom;
    private Date QMValidationDate;
    
    private boolean deleted;
    private Date dateArchivage;
}