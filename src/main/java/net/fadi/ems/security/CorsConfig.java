package net.fadi.ems.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class CorsConfig {
    /**
     * CORS Konfiguration.
     *
     * Definiert welche Origins, Methoden und Headers erlaubt sind.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        /*
         * Erlaubte Origins = welche Frontend-Domains dürfen Requests schicken?
         * 
         * Entwicklung: localhost:3000 (Next.js Dev Server)
         * Produktion: Deine echte Domain eintragen!
         *
         * NIEMALS "*" (alles erlauben) in Produktion nutzen!
         */
        config.setAllowedOrigins(List.of("http://localhost:3000"));

        /*
         * Erlaubte HTTP-Methoden.
         * GET, POST, PUT, DELETE, PATCH sind die Standard REST-Methoden.
         * OPTIONS ist für CORS Preflight-Requests notwendig!
         */
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        /*
         * Erlaubte Request-Headers.
         * Authorization: Für den JWT Token
         * Content-Type: Für JSON Body
         * Accept: Für Response-Format
         */
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        /*
         * allowCredentials = true erlaubt das Senden von Cookies mit Cross-Origin
         * Requests.
         * Wenn true, darf allowedOrigins NICHT "*" sein!
         */
        config.setAllowCredentials(true);

        // Diese Konfiguration gilt für alle URL-Pfade
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
