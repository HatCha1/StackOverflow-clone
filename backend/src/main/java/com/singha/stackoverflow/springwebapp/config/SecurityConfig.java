package com.singha.stackoverflow.springwebapp.config;

import com.singha.stackoverflow.springwebapp.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Cryptographically hashes passwords using BCrypt algorithm
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable Cross-Origin Resource Sharing
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF as tokens protect us from session-hijacks
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set app stateless
                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                        // Secures specific operational endpoints before the GET wildcards catch them
                        .requestMatchers("/api/questions/*/bookmark").authenticated()
                        .requestMatchers("/api/answers/*/accept").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/questions/*/answers").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/questions/**").permitAll() // Anyone can browse question feeds
                        .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()  // Anyone can read comments
                        .requestMatchers(HttpMethod.GET, "/api/answers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/badges/**").permitAll()

                        // Protected Endpoints (Require a valid Authorization Bearer header token)
                        .anyRequest().authenticated()
                );

        // Slide our custom token extraction filter right before Spring's native login filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://pasu-ssc-todolist.xyz",
                "http://pasu-ssc-todolist.xyz"
        )); // Whitelist React server ports + production domain
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}