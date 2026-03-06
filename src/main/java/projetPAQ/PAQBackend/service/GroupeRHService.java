package projetPAQ.PAQBackend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import projetPAQ.PAQBackend.DTO.UserDTO;
import projetPAQ.PAQBackend.entity.GroupeRH;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.GroupeRHRepository;
import projetPAQ.PAQBackend.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupeRHService {

    private final GroupeRHRepository groupeRHRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupeRHService(GroupeRHRepository groupeRHRepository, UserRepository userRepository) {
        this.groupeRHRepository = groupeRHRepository;
        this.userRepository = userRepository;
    }

    public GroupeRH createGroupeRH(GroupeRH groupeRH) {
        return groupeRHRepository.save(groupeRH);
    }

    public GroupeRH getGroupeRHById(Long id) {
        return groupeRHRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe RH non trouvé"));
    }

    public List<GroupeRH> getAllGroupesRH() {
        return groupeRHRepository.findAll();
    }

    public GroupeRH addMembreToGroupe(Long groupeId, Long userId) {
        GroupeRH groupe = getGroupeRHById(groupeId);
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        groupe.getMembresIds().add(userId);
        return groupeRHRepository.save(groupe);
    }
    public GroupeRH addMembreToGroupeByEmail(Long groupeId, String userEmail) {
        GroupeRH groupe = getGroupeRHById(groupeId);
        User user = groupeRHRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        
        groupe.getMembresIds().add(user.getId());
        return groupeRHRepository.save(groupe);
    }

    public GroupeRH removeMembreFromGroupe(Long groupeId, Long userId) {
        GroupeRH groupe = getGroupeRHById(groupeId);
        groupe.getMembresIds().remove(userId);
        return groupeRHRepository.save(groupe);
    }

    
    
  
    
    
    public List<GroupeRH> getGroupesByPlant(String plant) {
        return groupeRHRepository.findAll().stream()
                .filter(groupe -> plant.equals(groupe.getPlant()) || 
                       (groupe.getMembresIds() != null && !groupe.getMembresIds().isEmpty() && 
                        userRepository.findAllById(groupe.getMembresIds())
                            .stream()
                            .anyMatch(user -> plant.equals(user.getPlant()))))
                .collect(Collectors.toList());
    }

    public List<UserDTO> getMembresWithDetails(Long groupeId) {
        GroupeRH groupe = getGroupeRHById(groupeId);
        if (groupe.getMembresIds() == null || groupe.getMembresIds().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<User> users = userRepository.findAllById(groupe.getMembresIds());
        return users.stream()
                .map(this::convertToUserDTO)  // Convertir chaque User en UserDTO
                .collect(Collectors.toList());
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRole(user.getRole());
        userDTO.setPlant(user.getPlant());
        userDTO.setCreatedBy(user.getCreatedBy());
        userDTO.setSegment(user.getSegment());
        return userDTO;
    }
    public void deleteGroupeRH(Long id) {
        GroupeRH groupe = groupeRHRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe RH non trouvé avec l'ID: " + id));
        
        // Option 1: Vider les membres avant suppression
        groupe.getMembresIds().clear();
        groupeRHRepository.save(groupe);
        
        // Option 2: Supprimer directement (les relations @ElementCollection seront gérées automatiquement)
        groupeRHRepository.delete(groupe);
    }
    
}