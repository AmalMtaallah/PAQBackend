package projetPAQ.PAQBackend.DTO;

import java.sql.Date;
import lombok.Data;
@Data
public class EntretienDeMesureDTO {
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
    private Long validatedQMseegmetUserId;
    private String validatedQMseegmetNom;
    private String validatedQMseegmetPrenom;
    private Long validatedSGLUserId;
    private String validatedSGLNom;
    private String validatedSGLPrenom;
    private boolean deleted;
    private Date qmseegmetValidationDate;
    private Date sglValidationDate;
    private Date dateArchivage;
}