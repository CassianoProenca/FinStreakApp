package com.financial.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll() // Público
                        .requestMatchers("/api/transactions/**", "/api/gamification/**").permitAll() // Permitir acesso temporário para testes
                        .anyRequest().authenticated() // Todo o resto precisa de Token
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})); // Valida o Token JWT

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Dummy secret for development
        String secret = "u7i8o9p0q1w2e3r4t5y6u7i8o9p0q1w2"; 
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(secret.getBytes(), "HmacSHA256")).build();
    }
}