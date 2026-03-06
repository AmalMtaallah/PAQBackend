package projetPAQ.PAQBackend.DTO;



import lombok.Data;

@Data
public class DynamicEmailRequest {
	private String destinataire;
    private String sujet;
    private String message;

    // Getters et Setters
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
}