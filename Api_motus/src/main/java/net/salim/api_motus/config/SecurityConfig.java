package net.salim.api_motus.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Nouvelle syntaxe pour CSRF (Spring Security 6.1+)
                .csrf(csrf -> csrf.disable())

                // Nouvelle syntaxe pour les autorisations
                .authorizeHttpRequests(auth -> auth
                        // Autoriser H2 Console (si utilisé)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Autoriser toutes les requêtes vers l'API Motus
                        .requestMatchers("/api/**").permitAll()

                        // Autoriser la page d'accueil
                        .requestMatchers(HttpMethod.GET, "/").permitAll()

                        // Autoriser tous les endpoints pour les tests avec Postman
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Toutes les autres requêtes sont autorisées (pour développement)
                        .anyRequest().permitAll()
                )

                // Nouvelle syntaxe pour les headers
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}
