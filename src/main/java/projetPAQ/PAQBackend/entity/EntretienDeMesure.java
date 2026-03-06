package projetPAQ.PAQBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;

@Entity
@Data
public class EntretienDeMesure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;
    private String typeErreur;
    private String details;
    private String decision;
    private boolean deleted = false; // Champ pour le soft delete

    @ManyToOne
    @JoinColumn(name = "id_collaborateur")
    private Collaborateur collaborateur;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user; // Superviseur direct

    @ManyToOne
    @JoinColumn(name = "validated_qmseegmet_user_id")
    private User validatedQMseegmet; // Référence à l'utilisateur qui a validé QMseegmet

    @ManyToOne
    @JoinColumn(name = "validated_sgl_user_id")
    private User validatedSGL; // Référence à l'utilisateur qui a validé SGL
    
    @Column(name = "qmseegmet_validation_date")
    private Date qmseegmetValidationDate;

    @Column(name = "sgl_validation_date")
    private Date sglValidationDate;
    
    private boolean archive = false;
    @Temporal(TemporalType.DATE)
    private Date dateArchivage;
}