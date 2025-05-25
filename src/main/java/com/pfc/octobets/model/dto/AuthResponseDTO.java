package com.pfc.octobets.model.dto;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accessToken;

    private String tokenType = "Bearer";

    private Long idUsuario;

    private String email;

    private String nombre;

    private String apellidos;

    private String username;

    private String avatar;

    private Instant expiresAt;
}