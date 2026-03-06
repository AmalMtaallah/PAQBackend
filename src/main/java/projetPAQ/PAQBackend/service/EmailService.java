package projetPAQ.PAQBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import projetPAQ.PAQBackend.DTO.EmailResponse;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public EmailResponse envoyerEmail(String destinataire, String sujet, String message) {
        EmailResponse response = new EmailResponse();
        response.setDestinataire(destinataire);
        response.setSujet(sujet);
        response.setMessage(message);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(destinataire);
            helper.setSubject(sujet);
            helper.setText(message, true); // true pour activer le HTML
            helper.setFrom("amalmtaallah6@gmail.com");

            // Envoyer l'e-mail
            mailSender.send(mimeMessage);

            // Récupérer le messageId
            String messageId = mimeMessage.getMessageID();
            response.setMessageId(messageId);

            response.setStatus("SUCCESS");
            response.setDetails("E-mail envoyé avec succès à " + destinataire);
        } catch (MessagingException e) {
            response.setStatus("FAILURE");
            response.setDetails("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        } catch (Exception e) {
            response.setStatus("FAILURE");
            response.setDetails("Erreur inattendue : " + e.getMessage());
        }

        return response;
    }
}