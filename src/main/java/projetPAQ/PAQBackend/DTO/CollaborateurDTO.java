package projetPAQ.PAQBackend.DTO;

import java.util.Date;

public class CollaborateurDTO {
    private Integer id;
    private String nom;
    private String prenom;
    private Date dateEmbauche;
    private String seg;
    private UserDTO createur; // Utilisez UserDTO au lieu des champs séparés

    // Constructeur
    public CollaborateurDTO(Integer id, String nom, String prenom, Date dateEmbauche, 
                          String seg, UserDTO createur) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateEmbauche = dateEmbauche;
        this.seg = seg;
        this.createur = createur;
    }

    // Getters et Setters
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

    public UserDTO getCreateur() {
        return createur;
    }

    public void setCreateur(UserDTO createur) {
        this.createur = createur;
    }
}