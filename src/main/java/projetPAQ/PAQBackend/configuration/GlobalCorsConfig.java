package projetPAQ.PAQBackend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/*
@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Appliquer CORS globalement à toutes les routes API
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200") // Autoriser les requêtes provenant de localhost:4200
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes autorisées
                .allowedHeaders("*") // Autorise tous les headers
                .allowCredentials(true); // Permet les cookies ou les informations d'authentification
    }
}*/




import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

  /*  @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Apply CORS globally to all API routes
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200") // Allow requests from localhost:4200
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                .allowedHeaders("Authorization", "Content-Type", "Accept") // Allowed headers
                .allowCredentials(true); // Allow credentials (e.g., cookies, authorization headers)

        // Optionally, configure CORS for WebSocket endpoints
        registry.addMapping("/ws/**")
                .allowedOrigins("http://localhost:4200") // Allow WebSocket connections from localhost:4200
                .allowedMethods("*") // Allow all methods for WebSocket
                .allowedHeaders("*") // Allow all headers for WebSocket
                .allowCredentials(true); // Allow credentials for WebSocket
    }*/
	
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    //configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Autoriser le frontend
	    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://127.0.0.1:5500"));
	    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
	    configuration.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/ws/**", configuration); // Autoriser WebSocket
	    source.registerCorsConfiguration("/api/**", configuration);
	    return source;
	}
}