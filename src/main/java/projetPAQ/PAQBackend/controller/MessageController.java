package projetPAQ.PAQBackend.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import projetPAQ.PAQBackend.entity.Message;


@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // Envoyer un message public
    @MessageMapping("/application")
    @SendTo("/all/messages")
    public Message send(final Message message) throws Exception {
        return message;
    }

    // Envoyer un message privé
    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Message message) {
        System.out.println("Envoi d'un message privé à : " + message.getTo());
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/specific/messages", message);
    }

}