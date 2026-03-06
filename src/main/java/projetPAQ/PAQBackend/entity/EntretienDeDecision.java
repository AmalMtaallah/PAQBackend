package projetPAQ.PAQBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;

@Entity
@Data
public class EntretienDeDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;
    private String typeErreur;  // anciennement typeDecision
    private String details;     // anciennement description
    private String decision;    // anciennement justification
    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "id_collaborateur")
    private Collaborateur collaborateur;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user; // Superviseur direct

    // Validation HP seulement
    @ManyToOne
    @JoinColumn(name = "validated_hp_user_id")
    private User validatedHP;

    @Column(name = "hp_validation_date")
    private Date HPValidationDate;
    @ManyToOne
    @JoinColumn(name = "validated_qm_user_id")
    private User validatedQM;

    @Column(name = "qm_validation_date")
    private Date QMValidationDate;
    
    
    private boolean archive = false;
    @Temporal(TemporalType.DATE)
    private Date dateArchivage;
}