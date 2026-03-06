package projetPAQ.PAQBackend.DTO;



import lombok.Data;

@Data
public class DashboardStatsDTO {
    private long totalCollaborateurs;
    private long collaborateursArchives;
    private long totalEntretiensExplicatifs;
    private long totalEntretiensDaccord;
    private long totalEntretiensMesure;
    private long totalEntretiensDecision;
    private long totalEntretiensDecisionFinal;
    private long totalPhasesDialogue;
}


