package projetPAQ.PAQBackend.DTO;

import lombok.Data;

@Data
public class VerificationResponse {
    private Long sglId;
    private Integer collaborateurId;
    private Long slCreatorId;
    private boolean isValid;
    private String message;
}