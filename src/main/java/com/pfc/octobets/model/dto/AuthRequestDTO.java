package com.pfc.octobets.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** El email o username del usuario */
    private String login;

    /** La contraseña en texto plano */
    private String password;
}
