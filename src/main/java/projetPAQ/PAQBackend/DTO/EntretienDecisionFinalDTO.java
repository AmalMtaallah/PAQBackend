package projetPAQ.PAQBackend.DTO;

import java.util.Date;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

import lombok.Data;

@Data
public class EntretienDecisionFinalDTO {
    private Integer id;
    @NotNull
    private Date date;
    private String typeErreur;
    private String details;
    private String decisionFinale;
    
    private Integer collaborateurId;
    private String collaborateurNom;
    private String collaborateurPrenom;
    
    private Long userId;
    private String userNom;
    private String userPrenom;
    
    private Long groupeRHId;
    private String groupeRHNom;
    private List<String> groupeRHEmails;
    private List<Long> groupeRHMembresIds;
    
    private Long validatedRHUserId;
    private String validatedRHNom;
    private String validatedRHPrenom;
    private Date RHValidationDate;
    
    private boolean validationComplete;
    private Date dateArchivage;
}