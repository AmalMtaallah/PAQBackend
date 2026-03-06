package projetPAQ.PAQBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import projetPAQ.PAQBackend.configuration.JwtUtils;
import projetPAQ.PAQBackend.entity.Notification;
import projetPAQ.PAQBackend.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification, @RequestHeader("Authorization") String token) {
        try {
            String email = validateTokenAndGetEmail(token);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
            }
            if (notification.getTempsDeNotification() == null) {
                notification.setTempsDeNotification(LocalDateTime.now());
            }
            return ResponseEntity.ok(notificationService.saveNotification(notification));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    @GetMapping("/unread/{destinataireEmail}")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable String destinataireEmail, @RequestHeader("Authorization") String token) {
        try {
            String email = validateTokenAndGetEmail(token);
            if (email == null || !email.equals(destinataireEmail)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token or email mismatch");
            }
            return ResponseEntity.ok(notificationService.getUnreadNotifications(destinataireEmail));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    @GetMapping("/all/{destinataireEmail}")
    public ResponseEntity<?> getAllNotifications(@PathVariable String destinataireEmail, @RequestHeader("Authorization") String token) {
        try {
            String email = validateTokenAndGetEmail(token);
            if (email == null || !email.equals(destinataireEmail)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token or email mismatch");
            }
            return ResponseEntity.ok(notificationService.getAllNotifications(destinataireEmail));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    @PutMapping("/markAsRead/{notificationId}")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId, @RequestHeader("Authorization") String token) {
        try {
            String email = validateTokenAndGetEmail(token);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
            }
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    private String validateTokenAndGetEmail(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String jwtToken = token.substring(7); // Retirer "Bearer "
        return jwtUtils.extractUsername(jwtToken);
    }
    
    
    
    
 

    @PostMapping("/sendNotificationToUser")
    public void sendNotificationToUser(@RequestBody Map<String, String> notification) {
        String username = notification.get("username"); // Récupérer l'utilisateur cible
        String message = notification.get("message"); // Récupérer le message

        // Envoyer la notification à la queue spécifique de l'utilisateur
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }
    
    
    
    @PostMapping("/byEntretienAndMessage")
    public ResponseEntity<?> getNotificationsByEntretienAndMessage(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String token) {
        try {
            String email = validateTokenAndGetEmail(token);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
            }

            // Extraction des paramètres depuis la Map
            Long entretienId = Long.valueOf(requestBody.get("entretienId").toString());
            String message = (String) requestBody.get("message");

            List<String> emails = notificationService.getDestinataireEmailsByEntretienAndMessage(entretienId, message);
            
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error: " + e.getMessage());
        }
    }


}