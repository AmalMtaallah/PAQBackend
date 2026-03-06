package projetPAQ.PAQBackend.controller;



import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import projetPAQ.PAQBackend.DTO.EmailResponse;
import projetPAQ.PAQBackend.entity.DynamicEmailRequest;
import projetPAQ.PAQBackend.service.EmailService;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private SimpMessagingTemplate template;
    
    private Map<String, String> emailSenderMap = new ConcurrentHashMap<>();
   

  /*  private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    @PostMapping("/send")
    public ResponseEntity<EmailResponse> envoyerEmail(@RequestBody DynamicEmailRequest emailRequest) {
        EmailResponse emailResponse = emailService.envoyerEmail(
                emailRequest.getDestinataire(),
                emailRequest.getSujet(),
                emailRequest.getMessage()
        );

        // Retourner une réponse JSON avec le statut et l'ID du message
        return ResponseEntity.ok(emailResponse);
    }*/
    
   
    @PostMapping("/send")
    public ResponseEntity<EmailResponse> envoyerEmail(@RequestBody DynamicEmailRequest emailRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName(); // Récupérer l'email de l'utilisateur connecté

        // Stocker qui a envoyé l'email
        emailSenderMap.put(emailRequest.getDestinataire(), senderEmail);

        EmailResponse emailResponse = emailService.envoyerEmail(
                emailRequest.getDestinataire(),
                emailRequest.getSujet(),
                emailRequest.getMessage()
        );
System.out.print(emailResponse);
        return ResponseEntity.ok(emailResponse);
    }
    
    
    public String getExpediteur(String destinataire) {
        return emailSenderMap.get(destinataire);
    }
}
    


