package com.pfc.octobets.model.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class RegisterRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String apellidos;
    private String username;
    private String email;
    private String password;
}
