package projetPAQ.PAQBackend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.Data;
import projetPAQ.PAQBackend.repository.UserRepository;

@Entity
@Data
public class GroupeRH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nom;
    private String description;
    private String plant;
    
    @ElementCollection
    @CollectionTable(name = "groupe_rh_membres", joinColumns = @JoinColumn(name = "groupe_rh_id"))
    @Column(name = "user_id")
    private Set<Long> membresIds; // Stocke les IDs des membres
    
    @Transient // Non persisté en base
    private List<User> membres; // Pour faciliter l'utilisation
    
    public List<String> getMembresEmails(UserRepository userRepository) {
        return userRepository.findAllById(membresIds).stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }
}