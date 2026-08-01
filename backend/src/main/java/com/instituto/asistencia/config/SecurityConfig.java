package com.instituto.asistencia.config;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import java.util.List;
import com.instituto.asistencia.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
public SecurityFilterChain securityFilterChain(
        HttpSecurity http) throws Exception {

    http
        .cors(cors -> {})
        .csrf(csrf -> csrf.disable())

        .sessionManagement(session ->
                session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))

        .authorizeHttpRequests(auth -> auth

                // Login
                .requestMatchers(
                        "/api/auth/**"
                ).permitAll()

                // Solo ADMIN
                .requestMatchers(
                        "/api/usuarios/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        "/api/turnos/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        "/api/respaldos/**"
                ).hasRole("ADMIN")

                // ADMIN y SECRETARIO
                .requestMatchers(
                        "/api/asistencia/**"
                ).hasAnyRole(
                        "ADMIN",
                        "SECRETARIO"
                )

                .requestMatchers(
                        "/api/docentes/**"
                ).hasAnyRole(
                        "ADMIN",
                        "SECRETARIO"
                )

                .requestMatchers(
                        "/api/asignaciones/**"
                ).hasAnyRole(
                        "ADMIN",
                        "SECRETARIO"
                )

                .requestMatchers(
                        "/api/jornadas/**"
                ).hasAnyRole(
                        "ADMIN",
                        "SECRETARIO"
                )
                
                .requestMatchers(
                 "/api/reportes/**"
                ).hasAnyRole(
                        "ADMIN",
                        "SECRETARIO"
                )

                .anyRequest()
                .authenticated()
        )

        .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
}

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {

        return config.getAuthenticationManager();
    }
    @Bean
public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration =
            new CorsConfiguration();

    configuration.setAllowedOrigins(
            List.of(
                    "http://localhost:4200"
            ));

    configuration.setAllowedMethods(
            List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "OPTIONS"
            ));

    configuration.setAllowedHeaders(
            List.of("*"));

    configuration.setAllowCredentials(
            true);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
            "/**",
            configuration);

    return source;
}
}