package projetPAQ.PAQBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "collaborateurs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"}) // Assure que l'ID est unique
})
public class Collaborateur {

    @Id
    @Column(nullable = false, unique = true) // L'ID ne peut pas être null et doit être unique
    private Integer id;

    private String nom;
    private String prenom;
    
    @Temporal(TemporalType.DATE)
    private Date dateEmbauche;
    private String seg;
    
    @Temporal(TemporalType.DATE)
    private Date dernierArchivage; // Date du dernier archivage
    // Foreign Key Reference to User (creator of the collaborator)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false) // This column will store the reference to User
    private User user;
    
    private boolean deleted = false; // Nouveau champ pour la corbeille
    
    
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Getters and setters for the new 'user' field
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(Date dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public String getSeg() {
        return seg;
    }

    public void setSeg(String seg) {
        this.seg = seg;
    }
}