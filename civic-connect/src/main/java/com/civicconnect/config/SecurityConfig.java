package com.civicconnect.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Static files — everyone can access (API auth protects data)
                .requestMatchers("/", "/*.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // Auth endpoints — open
                .requestMatchers("/api/auth/**").permitAll()

                // Complaint submission & citizen tracking
                .requestMatchers("/api/complaints/my").hasRole("CITIZEN")
                .requestMatchers("/api/complaints/submit").hasRole("CITIZEN")
                .requestMatchers("/api/complaints/*/verify").hasRole("CITIZEN")
                .requestMatchers("/api/complaints/*/reopen").hasRole("CITIZEN")

                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Department endpoints
                .requestMatchers("/api/department/**").hasRole("DEPARTMENT")

                // Everything else needs authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
