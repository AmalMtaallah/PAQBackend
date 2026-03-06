package projetPAQ.PAQBackend.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import projetPAQ.PAQBackend.configuration.JwtUtils;

@RestController
@RequestMapping("/api/test")
public class TestController {

	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private JwtUtils jwtUtils;
	@GetMapping("/sendTestNotification")
    public ResponseEntity<?> sendTestNotification(@RequestHeader("Authorization") String token) {
        try {
            // Vérifiez si le token est valide
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
            }

            String jwtToken = token.substring(7); // Retirer le préfixe "Bearer "
            Claims claims = jwtUtils.extractAllClaims(jwtToken); // Extraire les claims du token

            // Extraire l'email du subject du token
            String email = claims.getSubject(); // Utiliser getSubject() pour obtenir l'email
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is missing in the token");
            }

            // Ici, vous pouvez éventuellement vérifier si l'utilisateur existe dans la base de données
            // Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            // if (!userOptional.isPresent()) {
            //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User  with the given email not found");
            // }

            // Créer la notification
            Map<String, String> notification = new HashMap<>();
            notification.put("type", "info");
            notification.put("message", "Ceci est une notification de test simulée");

            // Envoyer la notification à l'utilisateur
            messagingTemplate.convertAndSendToUser (email, "/queue/notifications", notification);

            return ResponseEntity.ok("Notification de test envoyée à " + email);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }
    
    
}
