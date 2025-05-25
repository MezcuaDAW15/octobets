package com.pfc.octobets.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pfc.octobets.repository.entity.Usuario;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.minutes}")
    private long expMinutes;

    private Key signingKey;

    @PostConstruct
    public void init() {
        // Inicializa la clave de firma a partir de la cadena secreta
        signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validate(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {

            return false;
        }
    }

    /**
     * Extrae el e-mail (username) del token **ya validado**.
     */
    public String getUsername(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extrae el id de usuario (claim <code>uid</code>) del token.
     */
    public Long getUserId(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey).build()
                .parseClaimsJws(token)
                .getBody()
                .get("uid", Long.class);
    }

    public String generateToken(UsuarioDetails ud) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(ud.getEmail()) // principal email
                .claim("uid", ud.getId())
                .claim("username", ud.getUsername())
                .claim("nombre", ud.getNombre())
                .claim("apellidos", ud.getApellidos())
                .claim("avatar", ud.getAvatar())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
