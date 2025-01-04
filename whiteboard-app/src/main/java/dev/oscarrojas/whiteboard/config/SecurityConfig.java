package dev.oscarrojas.whiteboard.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(authorize -> authorize
        .anyRequest().permitAll());

    http.cors(cors -> cors
        .configurationSource(corsConfigurationSource()));

    return http.build();
  }

  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration cors = new CorsConfiguration();
    cors.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
    cors.setAllowedMethods(Arrays.asList("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);

    return source;
  }
}
