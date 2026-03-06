package projetPAQ.PAQBackend.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import projetPAQ.PAQBackend.entity.*;
import projetPAQ.PAQBackend.repository.*;
import projetPAQ.PAQBackend.service.EmailService;
import projetPAQ.PAQBackend.service.EntretienExplicatifService;
import projetPAQ.PAQBackend.service.NotificationService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ArchivageScheduler {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private NotificationService notificationService;
    private final EntretienExplicatifService entretienExplicatifService;
    private final CollaborateurRepository collaborateurRepository;
    private final EntretienExplicatifRepository entretienExplicatifRepository;
    private final EntretienDaccordRepository entretienDaccordRepository;
    private final EntretienDeMesureRepository entretienDeMesureRepository;
    private final EntretienDeDecisionRepository entretienDeDecisionRepository;
    private final EntretienDecisionFinalRepository entretienDecisionFinalRepository;

    @Autowired
    public ArchivageScheduler(EntretienExplicatifService entretienExplicatifService,
                             CollaborateurRepository collaborateurRepository,
                             EntretienExplicatifRepository entretienExplicatifRepository,
                             EntretienDaccordRepository entretienDaccordRepository,
                             EntretienDeMesureRepository entretienDeMesureRepository,
                             EntretienDeDecisionRepository entretienDeDecisionRepository,
                             EntretienDecisionFinalRepository entretienDecisionFinalRepository) {
        this.entretienExplicatifService = entretienExplicatifService;
        this.collaborateurRepository = collaborateurRepository;
        this.entretienExplicatifRepository = entretienExplicatifRepository;
        this.entretienDaccordRepository = entretienDaccordRepository;
        this.entretienDeMesureRepository = entretienDeMesureRepository;
        this.entretienDeDecisionRepository = entretienDeDecisionRepository;
        this.entretienDecisionFinalRepository = entretienDecisionFinalRepository;
    }

    @Scheduled(cron = "0 33 12 * * ?") // Exécuter tous les jours à 14h44
    @Transactional
    public void archiveEntretiens() {
        Date aujourdHui = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aujourdHui);
        calendar.add(Calendar.MONTH, -6);
        Date dateLimiteNotification = calendar.getTime();
        calendar.setTime(aujourdHui);
        calendar.add(Calendar.MONTH, -6);
        Date dateLimiteArchivage = calendar.getTime();

        List<Collaborateur> collaborateursANotifier = new ArrayList<>();

        // Récupérer tous les collaborateurs non supprimés
        List<Collaborateur> collaborateurs = collaborateurRepository.findByDeletedFalse();

        for (Collaborateur collaborateur : collaborateurs) {
            Date derniereDateEntretien = trouverDerniereDateEntretienNonArchive(collaborateur);
            Date derniereDateArchivage = collaborateur.getDateEmbauche();

            if (collaborateur.getDernierArchivage() != null) {
                derniereDateArchivage = collaborateur.getDernierArchivage();
            }

            boolean aEuEntretienRecemment = derniereDateEntretien != null && derniereDateEntretien.after(dateLimiteNotification);
            boolean doitEtreArchive = derniereDateArchivage.before(dateLimiteArchivage) && !aEuEntretienRecemment;
            // Vérifier si le collaborateur doit être notifié
            if (doitEtreArchive && !estDansMemeTypeEntretienDepuisPlusDeSixMois(collaborateur, dateLimiteNotification)) {
                collaborateursANotifier.add(collaborateur);
            } else if (derniereDateEntretien != null && derniereDateEntretien.after(collaborateur.getDernierArchivage())) {
                collaborateur.setDernierArchivage(derniereDateEntretien);
                collaborateurRepository.save(collaborateur);
            }
            // Archiver les entretiens si nécessaire
            if (doitEtreArchive) {
                archiverEntretiensCollaborateur(collaborateur, aujourdHui);
                collaborateur.setDernierArchivage(aujourdHui);
                collaborateurRepository.save(collaborateur);
            }

           
        }

        // Envoyer les notifications (choisir l'une des deux options)
        // Option 1: Envoyer une notification individuelle pour chaque collaborateur
        for (Collaborateur collaborateur : collaborateursANotifier) {
            envoyerNotificationSGLIndividuelle(collaborateur);
        }

        // Option 2: Envoyer une notification avec la liste des IDs des collaborateurs
        if (!collaborateursANotifier.isEmpty()) {
            envoyerNotificationSGLGroupee(collaborateursANotifier);
        }
    }

    private Date trouverDerniereDateEntretienNonArchive(Collaborateur collaborateur) {
        Date derniereDate = null;

        List<EntretienExplicatif> entretiensExplicatifs = entretienExplicatifRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienExplicatif entretien : entretiensExplicatifs) {
            if (derniereDate == null || entretien.getDate().after(derniereDate)) {
                derniereDate = entretien.getDate();
            }
        }

        List<EntretienDaccord> entretiensDaccord = entretienDaccordRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienDaccord entretien : entretiensDaccord) {
            if (derniereDate == null || entretien.getDate().after(derniereDate)) {
                derniereDate = entretien.getDate();
            }
        }

        List<EntretienDeMesure> entretiensMesure = entretienDeMesureRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienDeMesure entretien : entretiensMesure) {
            if (derniereDate == null || entretien.getDate().after(derniereDate)) {
                derniereDate = entretien.getDate();
            }
        }

        List<EntretienDeDecision> entretiensDecision = entretienDeDecisionRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienDeDecision entretien : entretiensDecision) {
            if (derniereDate == null || entretien.getDate().after(derniereDate)) {
                derniereDate = entretien.getDate();
            }
        }

        List<EntretienDecisionFinal> entretiensDecisionFinal = entretienDecisionFinalRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienDecisionFinal entretien : entretiensDecisionFinal) {
            if (derniereDate == null || entretien.getDate().after(derniereDate)) {
                derniereDate = entretien.getDate();
            }
        }
        return derniereDate;
    }

    private boolean estDansMemeTypeEntretienDepuisPlusDeSixMois(Collaborateur collaborateur, Date dateLimite) {
        List<EntretienExplicatif> entretiensExplicatifs = entretienExplicatifRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        List<EntretienDaccord> entretiensDaccord = entretienDaccordRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        List<EntretienDeMesure> entretiensMesure = entretienDeMesureRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        List<EntretienDeDecision> entretiensDecision = entretienDeDecisionRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        List<EntretienDecisionFinal> entretiensDecisionFinal = entretienDecisionFinalRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);

        // Vérifier si tous les entretiens sont du même type et datent de plus de 6 mois
        if (!entretiensExplicatifs.isEmpty() && entretiensExplicatifs.stream().allMatch(e -> e.getDate().before(dateLimite))) {
            return true;
        }
        if (!entretiensDaccord.isEmpty() && entretiensDaccord.stream().allMatch(e -> e.getDate().before(dateLimite))) {
            return true;
        }
        if (!entretiensMesure.isEmpty() && entretiensMesure.stream().allMatch(e -> e.getDate().before(dateLimite))) {
            return true;
        }
        if (!entretiensDecision.isEmpty() && entretiensDecision.stream().allMatch(e -> e.getDate().before(dateLimite))) {
            return true;
        }
        if (!entretiensDecisionFinal.isEmpty() && entretiensDecisionFinal.stream().allMatch(e -> e.getDate().before(dateLimite))) {
            return true;
        }

        return false;
    }

    private void archiverEntretiensCollaborateur(Collaborateur collaborateur, Date dateArchivage) {
        // Archiver les entretiens explicatifs
        List<EntretienExplicatif> entretiensExplicatifs = entretienExplicatifRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienExplicatif entretien : entretiensExplicatifs) {
            entretien.setArchive(true);
            entretien.setDateArchivage(new java.sql.Date(dateArchivage.getTime()));
            entretienExplicatifRepository.save(entretien);
        }

        // Archiver les entretiens d'accord
        List<EntretienDaccord> entretiensDaccord = entretienDaccordRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienDaccord entretien : entretiensDaccord) {
            entretien.setArchive(true);
            entretien.setDateArchivage(new java.sql.Date(dateArchivage.getTime()));
            entretienDaccordRepository.save(entretien);
        }

        // Archiver les entretiens de mesure
        List<EntretienDeMesure> entretiensMesure = entretienDeMesureRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienDeMesure entretien : entretiensMesure) {
            entretien.setArchive(true);
            entretien.setDateArchivage(new java.sql.Date(dateArchivage.getTime()));
            entretienDeMesureRepository.save(entretien);
        }

        // Archiver les entretiens de décision
        List<EntretienDeDecision> entretiensDecision = entretienDeDecisionRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienDeDecision entretien : entretiensDecision) {
            entretien.setArchive(true);
            entretien.setDateArchivage(new java.sql.Date(dateArchivage.getTime()));
            entretienDeDecisionRepository.save(entretien);
        }

        // Archiver les entretiens de décision finale
        List<EntretienDecisionFinal> entretiensDecisionFinal = entretienDecisionFinalRepository
                .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
        for (EntretienDecisionFinal entretien : entretiensDecisionFinal) {
            entretien.setArchive(true);
            entretien.setDateArchivage(new java.sql.Date(dateArchivage.getTime()));
            entretienDecisionFinalRepository.save(entretien);
        }
    }

    private void envoyerNotificationSGLIndividuelle(Collaborateur collaborateur) {
        // Ici, vous implémenterez la logique pour envoyer une notification individuelle au SGL.
        System.out.println("Notification SGL: Le collaborateur (ID: " + collaborateur.getId() + ") " +
                collaborateur.getNom() + " " + collaborateur.getPrenom() +
                " n'a pas eu d'entretien depuis plus de 6 mois. " +
                "Tous ses entretiens ont été archivés et un nouveau cycle commence aujourd'hui.");
    }

  /*  private void envoyerNotificationSGLGroupee(List<Collaborateur> collaborateurs) {
        List<Integer> idsCollaborateurs = new ArrayList<>();
        for (Collaborateur collab : collaborateurs) {
            idsCollaborateurs.add(collab.getId());
        }

        System.out.println("Notification SGL: Les collaborateurs suivants (IDs: " + idsCollaborateurs +
                ") n'ont pas eu d'entretien depuis plus de 6 mois. " +
                "Tous leurs entretiens ont été archivés et un nouveau cycle commence aujourd'hui.");
    }*/
    
    
    private void envoyerNotificationSGLGroupee(List<Collaborateur> collaborateurs) {
        // Grouper les collaborateurs par leur SGL parent
        Map<User, List<Collaborateur>> collaborateursParSGL = collaborateurs.stream()
            .collect(Collectors.groupingBy(
                collaborateur -> {
                    // Trouver le SL qui a créé le collaborateur
                    User slCreator = collaborateur.getUser();
                    // Trouver le SGL parent du SL
                    return userRepository.findById(slCreator.getCreatedBy())
                        .orElseThrow(() -> new RuntimeException("SGL parent non trouvé pour le SL: " + slCreator.getId()));
                }
            ));

        // Envoyer une notification à chaque SGL avec ses collaborateurs concernés
        collaborateursParSGL.forEach((sgl, collaborateursDuSGL) -> {
            String collaborateurList = collaborateursDuSGL.stream()
                .map(c -> c.getId() + " - " + c.getNom() + " " + c.getPrenom())
                .collect(Collectors.joining("<br>"));

            String message = "Les collaborateurs suivants de votre équipe n'ont pas eu d'entretien depuis plus de 6 mois : <br>" 
                + collaborateurList 
                + "<br><br>Tous leurs entretiens ont été archivés et un nouveau cycle commence aujourd'hui.";

            // Envoyer l'email au SGL
            emailService.envoyerEmail(
                sgl.getEmail(), 
                "Collaborateurs sans entretien récent", 
                message
            );

            // Créer une notification pour le SGL
            Notification notification = new Notification();
            notification.setDestinataireEmail(sgl.getEmail());
            notification.setMessage(message);
            notification.setRead(false);
            notificationService.saveNotification(notification);
        });
    }
}
