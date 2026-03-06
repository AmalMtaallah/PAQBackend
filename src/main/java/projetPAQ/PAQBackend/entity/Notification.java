package projetPAQ.PAQBackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 1000)
    private String message;
    private boolean isRead;
    @Column(nullable = true)
    private Long entretienId;
    private String destinataireEmail;

    @CreationTimestamp
    private LocalDateTime tempsDeNotification; // Nouveau champ pour le timestamp de la notification

    // Getters and Setters
}