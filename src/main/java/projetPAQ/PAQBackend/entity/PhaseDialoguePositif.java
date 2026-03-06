package projetPAQ.PAQBackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
public class PhaseDialoguePositif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;
    private String commentaires;
    private boolean deleted = false; // Champ pour le soft delete
    @Column(name = "id_collaborateur", insertable = false, updatable = false)
    private Integer collaborateurId;
    
    @Column(name = "id_user", insertable = false, updatable = false)
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "id_collaborateur")
    private Collaborateur collaborateur;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user; // Superviseur direct
    
    
    private boolean archive = false;
    @Temporal(TemporalType.DATE)
    private Date dateArchivage;
}