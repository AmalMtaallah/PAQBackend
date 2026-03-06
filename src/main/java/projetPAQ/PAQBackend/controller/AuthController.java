package projetPAQ.PAQBackend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import projetPAQ.PAQBackend.DTO.ChangePasswordRequest;
import projetPAQ.PAQBackend.configuration.JwtUtils;
import projetPAQ.PAQBackend.entity.User;
import projetPAQ.PAQBackend.repository.UserRepository;
import projetPAQ.PAQBackend.service.CustomUserDetailsService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
   /* // Constructeur manuel
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                          JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }*/
   /* @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByEmail(user.getEmail()) != null) {
            response.put("error", "Email already exists");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        response.put("message", "User registered successfully");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }*/
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        // Vérifiez si l'email est déjà pris
        if (userRepository.findByEmail(user.getEmail()) != null) {
            response.put("error", "Email already exists");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Vérifiez si l'ID est déjà pris
        if (userRepository.existsById(user.getId())) {
            response.put("error", "ID already exists");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Validation pour le segment
        if ("QMSegment".equals(user.getRole()) && (user.getSegment() == null || user.getSegment().isEmpty())) {
            response.put("error", "Segment is required for QMSegment role");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if (!"QMSegment".equals(user.getRole())) {
            // S'assurer que le segment est null pour les autres rôles
            user.setSegment(null);
        }

        // Si le rôle est SL, on enregistre qui a créé cet utilisateur
        if ("SL".equals(user.getRole())) {
            if (authentication != null && authentication.isAuthenticated()) {
                User creator = userRepository.findByEmail(authentication.getName());
                if (creator != null) {
                    user.setCreatedBy(creator.getId());
                }
            }
        }

        // Hacher le mot de passe avant de l'enregistrer
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        response.put("message", "User registered successfully");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }


   /* @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            User authenticatedUser = userRepository.findByEmail(user.getEmail());
            String token = jwtUtils.generateToken(authenticatedUser.getEmail(), authenticatedUser.getRole());

            Map<String, Object> authData = new HashMap<>();
            authData.put("token", token);
            authData.put("type", "Bearer");
            authData.put("role", authenticatedUser.getRole()); // Retourner aussi le rôle

            return ResponseEntity.ok(authData);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }*/

    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            User authenticatedUser = userRepository.findByEmail(user.getEmail());
            String token = jwtUtils.generateToken(authenticatedUser.getEmail(), authenticatedUser.getRole());

            Map<String, Object> authData = new HashMap<>();
            authData.put("token", token);
            authData.put("type", "Bearer");
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", authenticatedUser.getId());
            userData.put("firstName", authenticatedUser.getFirstName());
            userData.put("lastName", authenticatedUser.getLastName());
            userData.put("email", authenticatedUser.getEmail());
            userData.put("role", authenticatedUser.getRole());
            userData.put("hireDate", authenticatedUser.getHireDate());
            userData.put("plant", authenticatedUser.getPlant());
            userData.put("createdBy", authenticatedUser.getCreatedBy());
            userData.put("segment", authenticatedUser.getSegment()); // Ajout du segment
            
            authData.put("user", userData);

            return ResponseEntity.ok(authData);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
    
    
    
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Get the currently authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("error", "User not authenticated");
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String userEmail = authentication.getName();

        // Verify the current password
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
            response.put("error", "Invalid current password");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Check if the new password and confirmation match
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            response.put("error", "New password and confirmation do not match");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Encode the new password
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

        // Update the user's password in the database
        User user = userRepository.findByEmail(userEmail);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        response.put("message", "Password updated successfully");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
    
    
    
    
    
    
    
    
 
}