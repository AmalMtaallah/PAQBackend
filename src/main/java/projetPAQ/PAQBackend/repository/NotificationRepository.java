package projetPAQ.PAQBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import projetPAQ.PAQBackend.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireEmailAndIsReadFalse(String destinataireEmail);
    List<Notification> findByDestinataireEmail(String destinataireEmail);

    @Query("SELECT n FROM Notification n WHERE n.entretienId = :entretienId AND n.message LIKE %:message%")
    List<Notification> findByEntretienIdAndMessage(@Param("entretienId") Long entretienId, @Param("message") String message);


}