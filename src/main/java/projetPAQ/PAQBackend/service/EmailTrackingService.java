package projetPAQ.PAQBackend.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class EmailTrackingService {

    // Stocke l'association entre l'identifiant temporaire et l'ID de l'utilisateur
    private Map<String, Long> emailTrackingMap = new ConcurrentHashMap<>();

    /**
     * Génère un identifiant temporaire et l'associe à l'utilisateur.
     */
    public String trackEmail(Long userId) {
        String emailId = UUID.randomUUID().toString(); // Génère un identifiant unique
        emailTrackingMap.put(emailId, userId); // Associe l'identifiant à l'utilisateur
        return emailId;
    }

    /**
     * Récupère l'ID de l'utilisateur associé à l'identifiant temporaire.
     */
    public Long getUserIdForEmail(String emailId) {
        return emailTrackingMap.get(emailId);
    }

    /**
     * Supprime l'entrée après traitement.
     */
    public void removeEmail(String emailId) {
        emailTrackingMap.remove(emailId);
    }
}
