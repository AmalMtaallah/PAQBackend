package projetPAQ.PAQBackend.entity;

public class DynamicEmailRequest {
    private String destinataire; // Recipient's email
    private String sujet;       // Email subject
    private String message;     // Email message
    private String expediteur;  // Sender's email

    // Getters and Setters
    public String getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }
}
