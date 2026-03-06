package projetPAQ.PAQBackend.DTO;

import lombok.Data;

@Data
public class EmailResponse {
    private String destinataire;
    private String sujet;
    private String message;
    private String status; // SUCCESS ou FAILURE
    private String details; // Détails supplémentaires
    private String messageId; // ID unique de l'e-mail
}