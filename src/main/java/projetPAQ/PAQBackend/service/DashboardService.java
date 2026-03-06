package projetPAQ.PAQBackend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projetPAQ.PAQBackend.repository.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final EntretienExplicatifRepository explicatifRepository;
    private final EntretienDaccordRepository daccordRepository;
    private final EntretienDeMesureRepository mesureRepository;
    private final EntretienDeDecisionRepository decisionRepository;
    private final EntretienDecisionFinalRepository decisionFinalRepository;
    private final CollaborateurRepository collaborateurRepository;

    @Autowired
    public DashboardService(EntretienExplicatifRepository explicatifRepository,
                            EntretienDaccordRepository daccordRepository,
                            EntretienDeMesureRepository mesureRepository,
                            EntretienDeDecisionRepository decisionRepository,
                            EntretienDecisionFinalRepository decisionFinalRepository,
                            CollaborateurRepository collaborateurRepository) {
        this.explicatifRepository = explicatifRepository;
        this.daccordRepository = daccordRepository;
        this.mesureRepository = mesureRepository;
        this.decisionRepository = decisionRepository;
        this.decisionFinalRepository = decisionFinalRepository;
        this.collaborateurRepository = collaborateurRepository;
    }

    // For totals (non supprimés et non archivés)
    public Map<String, Long> getTotalEntretienCounts() {
        Map<String, Long> counts = new HashMap<>();

        counts.put("Explicatif", explicatifRepository.countByDeletedFalseAndArchivedFalse());
        counts.put("D'accord", daccordRepository.countByDeletedFalseAndArchivedFalse());
        counts.put("De mesure", mesureRepository.countByDeletedFalseAndArchivedFalse());
        counts.put("De décision", decisionRepository.countByDeletedFalseAndArchivedFalse());
        counts.put("Décision finale", decisionFinalRepository.countByDeletedFalseAndArchivedFalse());

        return counts;
    }

    // For stats by period (existing)
    public Map<String, Long> getEntretienCounts(Date startDate, Date endDate) {
        Map<String, Long> counts = new HashMap<>();

        counts.put("Explicatif", explicatifRepository.countByDateBetweenAndDeletedFalse(startDate, endDate));
        counts.put("D'accord", daccordRepository.countByDateBetweenAndDeletedFalse(startDate, endDate));
        counts.put("De mesure", mesureRepository.countByDateBetweenAndDeletedFalse(startDate, endDate));
        counts.put("De décision", decisionRepository.countByDateBetweenAndDeletedFalse(startDate, endDate));
        counts.put("Décision finale", decisionFinalRepository.countByDateBetweenAndDeletedFalse(startDate, endDate));

        return counts;
    }

    // For the total number of collaborators
    public Long getTotalCollaborateursCount() {
        return collaborateurRepository.countByDeletedFalse();
    }
}
