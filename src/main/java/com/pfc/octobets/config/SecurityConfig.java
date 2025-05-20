package com.pfc.octobets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // deshabilita CSRF si no usas formularios web
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // permite todas las rutas
                )
                .httpBasic().disable() // deshabilita Basic Auth
                .formLogin().disable(); // deshabilita form-login

        return http.build();
    }
}
