package projetPAQ.PAQBackend.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import projetPAQ.PAQBackend.filter.JwtAuthenticationFilter;
import projetPAQ.PAQBackend.filter.JwtFilter;
import projetPAQ.PAQBackend.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtUtils jwtUtils) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
        		.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
        		// .and()
                .csrf(AbstractHttpConfigurer::disable)
                
                // Désactiver la protection CSRF si nécessaire (en général pour les API REST)
                .authorizeHttpRequests(auth -> 
                        // Autoriser l'accès sans authentification aux routes liées à l'authentification
                        auth.requestMatchers("/api/auth/*").permitAll() 
                        .requestMatchers("/api/webhook/brevo").permitAll()
                        .requestMatchers("/ws/**").permitAll() // Allow WebSocket connections
                      
                       
                        .requestMatchers("/api/test/*").authenticated()
                        .requestMatchers("/api/notifications/*").authenticated() // Allow WebSocket connections
                     
                       
                        
                        .requestMatchers("/api/collaborateurs/add").hasRole("SL") 
                        .requestMatchers("/api/collaborateurs/update").hasAuthority("ROLE_SL")
                        .requestMatchers("/api/collaborateurs/delete/{id}").hasAuthority("ROLE_SL")
                        
                        .requestMatchers("/api/collaborateurs/**").authenticated()
                        
                        
                        .requestMatchers("/api/entretiensExplicatif/add").hasAnyRole("SL", "SGL")
                        .requestMatchers("/api/entretiensExplicatif/update/{id}").hasAnyRole("SL", "SGL")
                        .requestMatchers("/api/entretiensExplicatif/delete/{id}").hasAnyRole("SL", "SGL")
                        .requestMatchers("/api/entretiensExplicatif/*").authenticated()
                        
                        .requestMatchers("/api/entretiensDeMesure/*").authenticated()
                        .requestMatchers("/api/entretiensDecision/*").authenticated()
                        .requestMatchers("/api/entretiensDecisionFinal/*").authenticated()
                        .requestMatchers("/api/groupesRH/*").authenticated()
                        
                        
                        .requestMatchers("/api/entretiensDaccord/add").hasAnyRole("SL", "SGL","QMSegment")
                        .requestMatchers("/api/entretiensDaccord/delete/{id}").hasAnyRole("SL", "SGL","QMSegment")
                        .requestMatchers("/api/entretiensDaccord/*").authenticated()    
                        
                        .requestMatchers("/api/email/*").authenticated()  
                        .requestMatchers("/api/users/*").authenticated()  
                        .requestMatchers("/api/dashboard/*").authenticated() 
           
                        // Toute autre requête nécessite une authentification
                        .anyRequest().authenticated())
                // Ajouter le filtre JWT avant le filtre d'authentification classique
                .addFilterBefore(new JwtAuthenticationFilter(customUserDetailsService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    
   /* @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/ws/**", configuration);
        return source;
    }*/
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/ws/**", configuration);
        return source;
    }
    
  

}