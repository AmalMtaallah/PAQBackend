package projetPAQ.PAQBackend.entity;




import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class EntretienDaccord {

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

   
 // Remplacer le booléen validated par :
    @ManyToOne
    @JoinColumn(name = "validated_by_user_id")
    private User validatedBy; // Référence à l'utilisateur qui a validé

    private Date validationDate; // Date de validation// Nouvel attribut pour l'état de validation
    
    private boolean archive = false;
    @Temporal(TemporalType.DATE)
    private Date dateArchivage;
    
}