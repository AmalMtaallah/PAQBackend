package projetPAQ.PAQBackend.service;

import projetPAQ.PAQBackend.DTO.EntretienExplicatifDTO;
import projetPAQ.PAQBackend.entity.Collaborateur;
import projetPAQ.PAQBackend.entity.EntretienDaccord;
import projetPAQ.PAQBackend.entity.EntretienDeDecision;
import projetPAQ.PAQBackend.entity.EntretienDeMesure;
import projetPAQ.PAQBackend.entity.EntretienDecisionFinal;
import projetPAQ.PAQBackend.entity.EntretienExplicatif;
import projetPAQ.PAQBackend.entity.Notification;
import projetPAQ.PAQBackend.entity.PhaseDialoguePositif;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.CollaborateurRepository;
import projetPAQ.PAQBackend.repository.EntretienDaccordRepository;
import projetPAQ.PAQBackend.repository.EntretienDeDecisionRepository;
import projetPAQ.PAQBackend.repository.EntretienDeMesureRepository;
import projetPAQ.PAQBackend.repository.EntretienDecisionFinalRepository;
import projetPAQ.PAQBackend.repository.EntretienExplicatifRepository;
import projetPAQ.PAQBackend.repository.PhaseDialoguePositifRepository;
import projetPAQ.PAQBackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntretienExplicatifService {

    @Autowired
    private final CollaborateurRepository collaborateurRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRepository userRepository;

    private final EntretienExplicatifRepository entretienExplicatifRepository;
    private final EntretienDaccordRepository entretienDaccordRepository;
    private final EntretienDecisionFinalRepository entretienDecisionFinalRepository;
    private final EntretienDeDecisionRepository entretienDeDecisionRepository;
    private final EntretienDeMesureRepository entretienDeMesureRepository;
    private final PhaseDialoguePositifRepository phaseDialoguePositifRepository;

    @Autowired
    public EntretienExplicatifService(
        EntretienExplicatifRepository entretienExplicatifRepository,
        CollaborateurRepository collaborateurRepository,
        EntretienDaccordRepository entretienDaccordRepository,
        EntretienDecisionFinalRepository entretienDecisionFinalRepository,
        EntretienDeDecisionRepository entretienDeDecisionRepository,
        EntretienDeMesureRepository entretienDeMesureRepository,
        PhaseDialoguePositifRepository phaseDialoguePositifRepository) {

        this.entretienExplicatifRepository = entretienExplicatifRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.entretienDaccordRepository = entretienDaccordRepository;
        this.entretienDecisionFinalRepository = entretienDecisionFinalRepository;
        this.entretienDeDecisionRepository = entretienDeDecisionRepository;
        this.entretienDeMesureRepository = entretienDeMesureRepository;
        this.phaseDialoguePositifRepository = phaseDialoguePositifRepository;
    }

    public EntretienExplicatif createEntretien(EntretienExplicatif entretien, String userRole) {
        if (!"SL".equals(userRole) && !"SGL".equals(userRole)) {
            throw new RuntimeException("Seuls les utilisateurs avec le rôle SL ou SGL peuvent ajouter un entretien explicatif.");
        }
        return entretienExplicatifRepository.save(entretien);
    }

    public Optional<EntretienExplicatif> updateEntretien(Integer id, EntretienExplicatif updatedEntretien) {
        if (entretienExplicatifRepository.existsById(id)) {
            updatedEntretien.setId(id);
            return Optional.of(entretienExplicatifRepository.save(updatedEntretien));
        }
        return Optional.empty();
    }

    public EntretienExplicatifDTO convertToDTO(EntretienExplicatif entretien) {
        EntretienExplicatifDTO dto = new EntretienExplicatifDTO();
        dto.setId(entretien.getId());
        dto.setDate(entretien.getDate());
        dto.setTypeErreur(entretien.getTypeErreur());
        dto.setDetails(entretien.getDetails());
        dto.setDecision(entretien.getDecision());
        dto.setCollaborateurId(entretien.getCollaborateur().getId());
        dto.setCollaborateurNom(entretien.getCollaborateur().getNom());
        dto.setCollaborateurPrenom(entretien.getCollaborateur().getPrenom());
        dto.setUserId(entretien.getUser().getId());
        dto.setUserNom(entretien.getUser().getFirstName());
        dto.setUserPrenom(entretien.getUser().getLastName());
        if (entretien.getValidatedBy() != null) {
            dto.setValidatedByUserId(entretien.getValidatedBy().getId());
            dto.setValidatedByUserNom(entretien.getValidatedBy().getFirstName());
            dto.setValidatedByUserPrenom(entretien.getValidatedBy().getLastName());
            dto.setValidationDate(entretien.getValidationDate());
        }
        dto.setDateArchivage(entretien.getDateArchivage());
        return dto;
    }

    public void deleteEntretien(Integer id) {
        Optional<EntretienExplicatif> entretienOptional = entretienExplicatifRepository.findById(id);
        if (entretienOptional.isPresent()) {
            EntretienExplicatif entretien = entretienOptional.get();
            entretien.setDeleted(true);
            entretienExplicatifRepository.save(entretien);
        }
    }

    public List<EntretienExplicatif> getAllEntretiens() {
        return entretienExplicatifRepository.findByDeletedFalse();
    }

    public Optional<EntretienExplicatif> getEntretienById(Integer id) {
        return entretienExplicatifRepository.findByIdAndDeletedFalse(id);
    }

    public List<EntretienExplicatif> getEntretiensByCollaborateurId(Integer collaborateurId) {
        List<EntretienExplicatif> entretiens = entretienExplicatifRepository.findByCollaborateurIdAndNotDeletedAndNotArchived(collaborateurId);
        return entretiens.stream()
                         .filter(entretien -> !entretien.isDeleted())
                         .collect(Collectors.toList());
    }

    public void restoreEntretien(Integer id) {
        Optional<EntretienExplicatif> entretienOptional = entretienExplicatifRepository.findById(id);
        if (entretienOptional.isPresent()) {
            EntretienExplicatif entretien = entretienOptional.get();
            entretien.setDeleted(false);
            entretienExplicatifRepository.save(entretien);
        }
    }

    public Optional<EntretienExplicatif> findById(Integer id) {
        return entretienExplicatifRepository.findById(id);
    }

    public Optional<EntretienExplicatif> validateEntretien(Integer id, User validatingUser) {
        Optional<EntretienExplicatif> entretienOptional = entretienExplicatifRepository.findById(id);

        if (entretienOptional.isPresent()) {
            EntretienExplicatif entretien = entretienOptional.get();
            entretien.setValidatedBy(validatingUser);
            entretien.setValidationDate(new Date(System.currentTimeMillis()));
            entretienExplicatifRepository.save(entretien);
            return Optional.of(entretien);
        }

        return Optional.empty();
    }

    public List<EntretienExplicatif> getEntretiensByUserIdAndNotDeleted(Integer userId) {
        return entretienExplicatifRepository.findByUserIdAndDeletedFalse(userId);
    }

    public List<EntretienExplicatif> getDeletedEntretiensByUserId(Integer userId) {
        return entretienExplicatifRepository.findByUserIdAndDeletedTrue(userId);
    }

    
    
    
    /*
    private boolean shouldArchive(EntretienExplicatif entretien, Collaborateur collaborateur) {
        if (entretien.isDeleted() || entretien.isArchive()) {
            return false; // Ne pas archiver si l'entretien est déjà supprimé ou archivé
        }

        java.util.Date currentDateUtil = Calendar.getInstance().getTime();
        Date currentDate = new Date(currentDateUtil.getTime());

        // Utiliser la date de dernier archivage du collaborateur
        Date dernierArchivage = (Date) collaborateur.getDernierArchivage();

        long diffInMillies = Math.abs(currentDate.getTime() - entretien.getDate().getTime());
        long diff = diffInMillies / (1000 * 60 * 60 * 24);

        // Comparer avec la date de dernier archivage
        if (dernierArchivage != null) {
            long diffArchivageInMillies = Math.abs(currentDate.getTime() - dernierArchivage.getTime());
            long diffArchivage = diffArchivageInMillies / (1000 * 60 * 60 * 24);
            return diffArchivage >= 180; // 180 jours = 6 mois
        }

        return diff >= 180; // 180 jours = 6 mois
    }

@Transactional
public void archiveOldEntretiens() {
    List<Collaborateur> collaborateurs = collaborateurRepository.findAll();
    List<Collaborateur> collaborateursSansEntretienRecent = new ArrayList<>();

    java.util.Date currentDateUtil = Calendar.getInstance().getTime();
    Date currentDate = new Date(currentDateUtil.getTime());

    for (Collaborateur collaborateur : collaborateurs) {
        List<EntretienExplicatif> entretiens = entretienExplicatifRepository
            .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);

        boolean hasRecentEntretien = entretiens.stream()
            .anyMatch(entretien -> shouldArchive(entretien, collaborateur));

        if (hasRecentEntretien) {
            archiveAllEntretiensForCollaborateur(collaborateur);
        }

        // Mettre à jour la date de dernier archivage uniquement si des entretiens ont été archivés
        if (hasRecentEntretien) {
            collaborateur.setDernierArchivage(currentDate);
            collaborateurRepository.save(collaborateur);
        }

        // Vérifier si le collaborateur n'a pas d'entretien récent et mettre à jour la date de dernier archivage si nécessaire
        if (!hasRecentEntretien) {
            Date dernierArchivage = (Date) collaborateur.getDernierArchivage();
            if (dernierArchivage != null) {
                long diffArchivageInMillies = Math.abs(currentDate.getTime() - dernierArchivage.getTime());
                long diffArchivage = diffArchivageInMillies / (1000 * 60 * 60 * 24);
                if (diffArchivage >= 180) { // 180 jours = 6 mois
                    collaborateur.setDernierArchivage(currentDate);
                    collaborateurRepository.save(collaborateur);
                }
            }
            collaborateursSansEntretienRecent.add(collaborateur);
        }
    }

    if (!collaborateursSansEntretienRecent.isEmpty()) {
        notifyCollaborateursWithoutRecentEntretien(collaborateursSansEntretienRecent);
    }
}



    private void notifyCollaborateursWithoutRecentEntretien(List<Collaborateur> collaborateurs) {
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

            String message = "Les collaborateurs suivants de votre équipe n'ont pas eu d'entretien explicatif pendant les 6 derniers mois : <br>" + collaborateurList;
System.out.print(sgl.getEmail()+message);
            // Envoyer l'email au SGL
            emailService.envoyerEmail(sgl.getEmail(), 
                                   "Collaborateurs sans entretien explicatif récent", 
                                   message);

            // Créer une notification pour le SGL
            Notification notification = new Notification();
            notification.setDestinataireEmail(sgl.getEmail());
            notification.setMessage(message);
            notification.setRead(false);
            notificationService.saveNotification(notification);
        });
    }

  private void archiveAllEntretiensForCollaborateur(Collaborateur collaborateur) {
    java.util.Date currentDateUtil = Calendar.getInstance().getTime();
    Date currentDate = new Date(currentDateUtil.getTime());

    List<EntretienExplicatif> entretiensExplicatifs = entretienExplicatifRepository
        .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
    for (EntretienExplicatif entretien : entretiensExplicatifs) {
        entretien.setArchive(true);
        entretien.setDateArchivage(currentDate);
        entretienExplicatifRepository.save(entretien);
    }

    List<EntretienDaccord> entretiensDaccord = entretienDaccordRepository
        .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
    for (EntretienDaccord entretien : entretiensDaccord) {
        entretien.setArchive(true);
        entretien.setDateArchivage(currentDate);
        entretienDaccordRepository.save(entretien);
    }

    List<EntretienDecisionFinal> entretiensDecisionFinal = entretienDecisionFinalRepository
        .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
    for (EntretienDecisionFinal entretien : entretiensDecisionFinal) {
        entretien.setArchive(true);
        entretien.setDateArchivage(currentDate);
        entretienDecisionFinalRepository.save(entretien);
    }

    List<EntretienDeDecision> entretiensDeDecision = entretienDeDecisionRepository
        .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
    for (EntretienDeDecision entretien : entretiensDeDecision) {
        entretien.setArchive(true);
        entretien.setDateArchivage(currentDate);
        entretienDeDecisionRepository.save(entretien);
    }

    List<EntretienDeMesure> entretiensDeMesure = entretienDeMesureRepository
        .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
    for (EntretienDeMesure entretien : entretiensDeMesure) {
        entretien.setArchive(true);
        entretien.setDateArchivage(currentDate);
        entretienDeMesureRepository.save(entretien);
    }

    List<PhaseDialoguePositif> phasesDialoguePositif = phaseDialoguePositifRepository
        .findByCollaborateurAndDeletedFalseAndArchiveFalse(collaborateur);
    for (PhaseDialoguePositif phase : phasesDialoguePositif) {
        phase.setArchive(true);
        phase.setDateArchivage(currentDate);
        phaseDialoguePositifRepository.save(phase);
    }
}

    public void notifyAndEmailCollaborateursWithoutEntretien() {
        List<Collaborateur> collaborateurs = getCollaborateursWithoutEntretienInLast6Months();

        if (!collaborateurs.isEmpty()) {
            String collaborateurList = collaborateurs.stream()
                    .map(collaborateur -> collaborateur.getId() + " - " + collaborateur.getNom() + " " + collaborateur.getPrenom())
                    .collect(Collectors.joining("<br>"));

            String message = "Les collaborateurs suivants n'ont pas eu d'entretien explicatif pendant les 6 derniers mois : <br>" + collaborateurList;

            String emailDestinataire = "amalmtaallah6@gmail.com";
            String emailSujet = "Collaborateurs sans entretien explicatif";
            emailService.envoyerEmail(emailDestinataire, emailSujet, message);

            Notification notification = new Notification();
            notification.setDestinataireEmail(emailDestinataire);
            notification.setMessage(message);
            notification.setRead(false);
            notificationService.saveNotification(notification);
        }
    }

    public List<Collaborateur> getCollaborateursWithoutEntretienInLast6Months() {
        List<Collaborateur> allCollaborateurs = collaborateurRepository.findAll();
        java.util.Date currentDateUtil = Calendar.getInstance().getTime();
        Date currentDate = new Date(currentDateUtil.getTime());

        return allCollaborateurs.stream()
                .filter(collaborateur -> {
                    List<EntretienExplicatif> entretiens = entretienExplicatifRepository.findByCollaborateurAndDeletedFalse(collaborateur);
                    return entretiens.stream()
                            .noneMatch(entretien -> {
                                long diffInMillies = Math.abs(currentDate.getTime() - entretien.getDate().getTime());
                                long diff = diffInMillies / (1000 * 60 * 60 * 24);
                                return diff <= 180; // 180 jours = 6 mois
                            });
                })
                .collect(Collectors.toList());
    }*/
    
    public List<EntretienExplicatif> getArchivedEntretiens() {
        return entretienExplicatifRepository.findByArchiveTrueAndDeletedFalse();
    }

    public List<EntretienExplicatifDTO> getArchivedEntretiensDTO() {
        List<EntretienExplicatif> archivedEntretiens = getArchivedEntretiens();
        return archivedEntretiens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    
    public List<EntretienExplicatif> getDeletedAndNotArchivedEntretiens() {
        return entretienExplicatifRepository.findByDeletedTrueAndArchiveFalse();
    }
}
