package projetPAQ.PAQBackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import projetPAQ.PAQBackend.entity.Notification;
import projetPAQ.PAQBackend.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(String destinataireEmail) {
        return notificationRepository.findByDestinataireEmailAndIsReadFalse(destinataireEmail);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    
    public List<Notification> getAllNotifications(String destinataireEmail) {
        return notificationRepository.findByDestinataireEmail(destinataireEmail);
    }
    
    
    public List<Notification> getNotificationsByEntretienAndMessage(Long entretienId, String message) {
        return notificationRepository.findByEntretienIdAndMessage(entretienId, message);
    }
    
    // Pour récupérer seulement les emails des destinataires
    public List<String> getDestinataireEmailsByEntretienAndMessage(Long entretienId, String message) {
        return notificationRepository.findByEntretienIdAndMessage(entretienId, message)
                .stream()
                .map(Notification::getDestinataireEmail)
                .distinct() // pour éviter les doublons
                .collect(Collectors.toList());
    }
}
