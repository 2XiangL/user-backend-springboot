package com.movie.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/movies/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/movies/*/rating").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/movies/*/favorites").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/movies/*/favorites").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/movies/*/favorites").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/movies/*/comments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/movies/*/comments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/movies/*/ratings").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/movies/comments").permitAll()
                .anyRequest().permitAll()
            );

        return http.build();
    }
}

