package projetPAQ.PAQBackend.DTO;

import lombok.Data;

@Data
public class EntretienStatsDTO {
    private long explicatif;
    private long daccord;
    private long mesure;
    private long decision;
    private long decisionFinal;
    private long dialoguePositif;
}

