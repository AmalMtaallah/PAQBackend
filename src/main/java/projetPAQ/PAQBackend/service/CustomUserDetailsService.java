package projetPAQ.PAQBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import projetPAQ.PAQBackend.DTO.SglWithSlDTO;
import projetPAQ.PAQBackend.DTO.UserDTO;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	  private final UserRepository userRepository;
	   // private final PasswordEncoder passwordEncoder;

	  
    
    // Constructeur généré manuellement
    /*public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }*/
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
    
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setPlant(user.getPlant());
        dto.setCreatedBy(user.getCreatedBy());
        return dto;
    }
    
    public String getUserEmailById(Long userId) {
        return userRepository.findById(userId)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }
    
    
    
    public List<UserDTO> getQMPlantHPAndRHUsersFromSamePlant(Long userId) {
        // Récupérer l'utilisateur par son ID
        User requestingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Vérifier que l'utilisateur a une plante définie
        if (requestingUser.getPlant() == null || requestingUser.getPlant().isEmpty()) {
            throw new RuntimeException("User has no plant assigned");
        }
        
        // Définir les rôles à rechercher
        List<String> targetRoles = List.of("QMPlant", "HP");
        
        // Récupérer les utilisateurs avec les rôles cibles et la même plante
        List<User> users = userRepository.findByPlantAndRoleIn(
            requestingUser.getPlant(), 
            targetRoles
        );
        
        // Convertir en DTO
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    
    public List<SglWithSlDTO> getAllSglWithSlList() {
        List<User> sglUsers = userRepository.findByRole("SGL");

        return sglUsers.stream().map(sgl -> {
            List<User> slUsers = userRepository.findByCreatedBy(sgl.getId());

            List<UserDTO> slDTOs = slUsers.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return new SglWithSlDTO(sgl.getId(), sgl.getFirstName(), sgl.getLastName(), slDTOs);
        }).collect(Collectors.toList());
    }
    
 // Dans CustomUserDetailsService.java

  /*  public void changeUserPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean verifyCurrentPassword(String email, String currentPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }*/
    
    
    
    
 
    
}