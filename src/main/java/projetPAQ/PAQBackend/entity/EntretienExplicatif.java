package projetPAQ.PAQBackend.entity;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Data
public class EntretienExplicatif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;
    private String typeErreur;
    private String details;
    private String decision;

    @ManyToOne
    @JoinColumn(name = "id_collaborateur")
    private Collaborateur collaborateur; // L'entité Collaborateur

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user; // L'entité SL (Superviseur Ligne ou autre)
    
    private boolean deleted = false; // Nouvel attribut pour la corbeille
 // Remplacer le boolean validated par :
    @ManyToOne
    @JoinColumn(name = "validated_by_user_id")
    private User validatedBy; // L'utilisateur qui a validé l'entretien

    @Column(name = "validation_date")
    private Date validationDate; // Date de validation// Nouvel attribut pour l'état de validation
    
 private boolean archive = false;
    
    @Temporal(TemporalType.DATE)
    private Date dateArchivage;
  
    // Getters et setters
    
    
}

