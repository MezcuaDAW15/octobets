package com.pfc.octobets.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pfc.octobets.security.JwtFilter;
import com.pfc.octobets.service.UsuarioDetailsServiceImpl;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UsuarioDetailsServiceImpl usuarioDetailsService;

    // @Bean
    // public SecurityFilterChain chain(HttpSecurity http, JwtFilter jwtFilter)
    // throws Exception {
    // http
    // .csrf(csrf -> csrf.disable())
    // .sessionManagement(sm ->
    // sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers("/auth/**").permitAll() // rutas públicas
    // .anyRequest().authenticated())
    // .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    // return http.build();
    // }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:4200"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    SecurityFilterChain chain(HttpSecurity http, JwtFilter jwt) throws Exception {
        http
                .cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/auth/**").permitAll() // ← público
                        .requestMatchers(HttpMethod.GET, "/ws/apuestas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ws/apuestas/**").permitAll()
                        .requestMatchers("/ws/carteras/stripe/**").permitAll()
                        .anyRequest().authenticated() // ← resto protegido
                )
                .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(
            HttpSecurity http, UsuarioDetailsServiceImpl uds, PasswordEncoder enc) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(uds)
                .passwordEncoder(enc)
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
