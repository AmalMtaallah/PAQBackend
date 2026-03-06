package projetPAQ.PAQBackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import projetPAQ.PAQBackend.configuration.JwtUtils;
import projetPAQ.PAQBackend.service.CustomUserDetailsService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

   /* public JwtAuthenticationFilter(CustomUserDetailsService customUserDetailsService, JwtUtils jwtUtils) {
		super();
		this.customUserDetailsService = customUserDetailsService;
		this.jwtUtils = jwtUtils;
	}
*/
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Extraire l'en-tête d'autorisation
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String email = null;

        // Vérifier si le header "Authorization" existe et commence par "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);  // Extraire le token sans "Bearer "
            email = jwtUtils.extractUsername(jwt);  // Extraire l'email à partir du JWT
        }

        // Si l'email est trouvé et qu'il n'y a pas encore d'authentification dans le SecurityContext
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Charger les détails de l'utilisateur avec l'email
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // Si le token est valide
            if (jwtUtils.validateToken(jwt, userDetails)) {
                // Créer un objet d'authentification avec les rôles de l'utilisateur
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // Ajouter l'authentification au SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuer la chaîne des filtres
        filterChain.doFilter(request, response);
    }
}
