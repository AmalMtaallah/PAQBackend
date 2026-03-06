package projetPAQ.PAQBackend.configuration;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
	 private final String secretKey;
	    private final long expirationTime;

	    public JwtUtils(@Value("${app.secret-key}") String secretKey, 
	                    @Value("${app.expiration-time}") long expirationTime) {
	        this.secretKey = secretKey;
	        this.expirationTime = expirationTime;
	    }
    @PostConstruct
    public void init() {
        if (secretKey == null) {
            throw new IllegalStateException("La clé secrète n'est pas initialisée. Vérifiez votre fichier de configuration.");
        }
        System.out.println("Clé secrète injectée: " + secretKey);
    }

    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + role); // Ajouter le préfixe ROLE_
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String email = extractUsername(token);
        boolean isExpired = isTokenExpired(token);
        System.out.println("Email extrait: " + email);
        System.out.println("Email utilisateur: " + userDetails.getUsername());
        System.out.println("Token expiré: " + isExpired);
        return (email.equals(userDetails.getUsername()) && !isExpired);
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSignKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("Erreur lors de l'extraction des claims: " + e.getMessage());
            throw e; // ou gérer l'exception comme vous le souhaitez
        }
    }
    
    
    // Validate token without UserDetails (only checks if the token is valid)
    public Boolean validateToken(String token) {
        try {
            // Extract claims and check if the token is expired
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }
    
    
 // New method to create an Authentication object from the JWT token
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.getSubject(); // Extract the email (subject) from the token
        String role = (String) claims.get("role"); // Extract the role from the token

        // Create a list of authorities (roles) from the token
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        // Create and return the Authentication object
        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }
}