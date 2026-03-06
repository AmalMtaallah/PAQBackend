package projetPAQ.PAQBackend.service;


import projetPAQ.PAQBackend.entity.Collaborateur;

public class CollaborateurUpdateRequest {
    private Integer id; // ID du collaborateur à mettre à jour
    private Collaborateur collaborateur; // Données de mise à jour

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Collaborateur getCollaborateur() {
        return collaborateur;
    }

    public void setCollaborateur(Collaborateur collaborateur) {
        this.collaborateur = collaborateur;
    }
}