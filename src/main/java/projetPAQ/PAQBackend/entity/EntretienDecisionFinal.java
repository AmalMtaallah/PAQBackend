package projetPAQ.PAQBackend.entity;



import java.util.Date;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;


@Entity
@Data
public class EntretienDecisionFinal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private Date date;
    private String typeErreur;
    private String details;
    private String decisionFinale;
    private boolean deleted = false;
    private boolean validationComplete = false;
    
    @ManyToOne
    private Collaborateur collaborateur;
    
    @ManyToOne
    private User user; // Superviseur qui a créé l'entretien
    
    @ManyToOne
    private User validatedRH; // RH qui a validé
    
    private Date RHValidationDate;
    
    @ManyToOne
    private GroupeRH groupeRH; // Groupe assigné pour validation
    
    private boolean archive = false;
    @Temporal(TemporalType.DATE)
    private Date dateArchivage;
}
