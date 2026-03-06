package projetPAQ.PAQBackend.DTO;



import lombok.Data;

@Data
public class CollaborateurCreatorInfoDTO {
    private Long slId;          // ID du SL qui a créé le collaborateur
    private String slFirstName; // Prénom du SL
    private String slLastName;  // Nom du SL
    private Long sglId;         // ID du SGL qui a créé le SL
    private String sglFirstName; // Prénom du SGL
    private String sglLastName;  // Nom du SGL
}