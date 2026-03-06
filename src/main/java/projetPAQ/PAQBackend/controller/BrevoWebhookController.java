package projetPAQ.PAQBackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projetPAQ.PAQBackend.entity.Notification;
import projetPAQ.PAQBackend.service.NotificationService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook/brevo")
public class BrevoWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(BrevoWebhookController.class);

    @Autowired
    private SimpMessagingTemplate template;
  
    
    @Autowired
    private EmailController emailController; // Pour accéder à emailSenderMap
    

    @Autowired
    private NotificationService notificationService; // Service pour enregistrer les notifications

   /* @PostMapping
    public ResponseEntity<String> handleBrevoWebhook(@RequestBody Map<String, Object> payload) {
        logger.info("Payload reçu: {}", payload);

        // Vérification des champs obligatoires
        String email = (String) payload.get("email");
        String event = (String) payload.get("event");
        String subject = (String) payload.get("subject");

        if (email == null || event == null || subject == null) {
            logger.error("Champs manquants dans le payload: email, event, ou subject");
            return ResponseEntity.badRequest().body("Champs manquants dans le payload");
        }

        // Création de l'objet JSON pour la notification
        Map<String, String> notification = new HashMap<>();
        switch (event) {
            case "delivered":
                notification.put("type", "delivered");
                notification.put("message", "✅ Email délivré: " + email + " - Sujet: " + subject);
                break;
            case "hard_bounce":
            case "soft_bounce":
            case "blocked":
            case "invalid_email":
                notification.put("type", "error");
                notification.put("message", "❌ Échec de livraison: " + email + " - Sujet: " + subject + " - Raison: " + event);
                break;
            default:
                logger.info("Événement ignoré: {}", event);
                return ResponseEntity.ok("Événement ignoré");
        }

        // Envoyer la notification au format JSON via WebSocket
        try {
            template.convertAndSend("/topic/notifications", notification);
            logger.info("Notification envoyée via WebSocket: {}", notification);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la notification via WebSocket", e);
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi de la notification");
        }

        return ResponseEntity.ok("Webhook reçu et traité.");
    }
	*/
    
    
   /* @PostMapping
    public ResponseEntity<String> handleBrevoWebhook(@RequestBody Map<String, Object> payload) {
        logger.info("Payload reçu: {}", payload);

        // Récupérer les données
        String destinataire = (String) payload.get("email"); // Celui qui a reçu le mail
        String event = (String) payload.get("event");
        String subject = (String) payload.get("subject");

        if (destinataire == null || event == null || subject == null) {
            logger.error("Champs manquants dans le payload");
            return ResponseEntity.badRequest().body("Champs manquants dans le payload");
        }

        // Trouver l'expéditeur
       String expediteur = emailController.getExpediteur(destinataire);
        if (expediteur == null) {
            logger.warn("Aucun expéditeur trouvé pour {}", destinataire);
            return ResponseEntity.ok("Expéditeur inconnu");
        }

        // Log pour vérifier l'expéditeur
        //System.out.println("Expéditeur récupéré : " + expediteur);

        // Créer la notification
        Map<String, String> notification = new HashMap<>();
        switch (event) {
            case "delivered":
                notification.put("type", "delivered");
                notification.put("message", "✅ Email livré: " + subject);
                break;
            case "hard_bounce":
            case "soft_bounce":
            case "blocked":
            case "invalid_email":
                notification.put("type", "error");
                notification.put("message", "❌ Échec de livraison: " + subject + " - Raison: " + event);
                break;
            default:
                logger.info("Événement ignoré: {}", event);
                return ResponseEntity.ok("Événement ignoré");
        }

        // Envoyer la notification à l'expéditeur seulement
        try {
            template.convertAndSendToUser(expediteur, "/queue/notifications", notification);
            logger.info("Notification envoyée à {} via WebSocket: {}", expediteur, notification);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la notification", e);
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi de la notification");
        }

        return ResponseEntity.ok("Webhook reçu et traité.");
    }  */
    
    
    
    @PostMapping
    public ResponseEntity<String> handleBrevoWebhook(@RequestBody Map<String, Object> payload) {
    	 // Forcez l'affichage du payload dans la console
        System.out.println("=== PAYLOAD RECU ===");
        System.out.println(payload);
        System.out.println("===================");
        logger.info("Payload reçu: {}", payload);
        // Récupération des champs avec vérification null
        String destinataire = payload.containsKey("email") ? (String) payload.get("email") : null;
        String event = payload.containsKey("event") ? (String) payload.get("event") : null;
        String subject = payload.containsKey("subject") ? (String) payload.get("subject") : null;

        System.out.println("Destinataire: " + destinataire);
        System.out.println("Event: " + event);
        System.out.println("Subject: " + subject);
        // Récupérer les données
     /*   String destinataire = (String) payload.get("email"); // Celui qui a reçu le mail
        String event = (String) payload.get("event");
        String subject = (String) payload.get("subject");
*/
        if (destinataire == null || event == null || subject == null) {
            logger.error("Champs manquants dans le payload");
            return ResponseEntity.badRequest().body("Champs manquants dans le payload");
        }

        // Trouver l'expéditeur
        String expediteur = emailController.getExpediteur(destinataire);
        if (expediteur == null) {
            logger.warn("Aucun expéditeur trouvé pour {}", destinataire);
            return ResponseEntity.ok("Expéditeur inconnu");
        }

        // Créer la notification
        Map<String, String> notification = new HashMap<>();
        String message = "";
        String type = "";

        switch (event) {
            case "delivered":
                type = "delivered";
                message = "✅ Email livré: " + subject + " - Destinataire: " + destinataire;;
                break;
            case "hard_bounce":
            case "soft_bounce":
            case "blocked":
            case "invalid_email":
                type = "error";
                message = "❌ Échec de livraison: " + subject + " - Destinataire: " + destinataire;
                break;
            default:
                logger.info("Événement ignoré: {}", event);
                return ResponseEntity.ok("Événement ignoré");
        }
        System.out.println("expediteur "+ expediteur);
      /*  Notification newNotification = new Notification();
        newNotification.setMessage(message);
        newNotification.setRead(false);
        newNotification.setDestinataireEmail(destinataire);
        newNotification.setTempsDeNotification(LocalDateTime.now()); // Important

        try {
            Notification saved = notificationService.saveNotification(newNotification);
            System.out.println("Notification sauvegardée avec ID: " + saved.getId());
        } catch (Exception e) {
            System.out.println("Erreur sauvegarde notification:");
            e.printStackTrace();
        }*/
        // Enregistrer la notification dans la base de données
      /*  Notification newNotification = new Notification();
        newNotification.setMessage(message);
        newNotification.setRead(false); // Par défaut, la notification est non lue
        newNotification.setDestinataireEmail(expediteur); // L'expéditeur reçoit la notification
        notificationService.saveNotification(newNotification);
        logger.info("Notification à enregistrer: {}", newNotification);*/

      /*  // Envoyer la notification à l'expéditeur via WebSocket
        notification.put("type", type);
        notification.put("message", message);

        try {
            template.convertAndSendToUser(expediteur, "/queue/notifications", notification);
            logger.info("Notification envoyée à {} via WebSocket: {}", expediteur, notification);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la notification", e);
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi de la notification");
        }*/

        return ResponseEntity.ok("Webhook reçu et traité.");
    }

	
}