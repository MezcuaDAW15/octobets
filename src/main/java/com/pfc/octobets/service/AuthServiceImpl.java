package com.pfc.octobets.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pfc.octobets.common.ApiException;
import com.pfc.octobets.common.ErrorCode;
import com.pfc.octobets.model.dto.AuthRequestDTO;
import com.pfc.octobets.model.dto.AuthResponseDTO;
import com.pfc.octobets.model.dto.RegisterRequestDTO;
import com.pfc.octobets.model.dto.UsuarioDTO;
import com.pfc.octobets.model.mapper.UsuarioMapper;
import com.pfc.octobets.repository.dao.UsuarioRepository;
import com.pfc.octobets.repository.entity.Cartera;
import com.pfc.octobets.repository.entity.Usuario;
import com.pfc.octobets.security.JwtProvider;
import com.pfc.octobets.security.UsuarioDetails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UsuarioMapper usuarioMapper;

    private final static String DEFAULT_AVATAR = "avatar-default.png";

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO req) {

        if (!usuarioRepository.findByEmail(req.getEmail()).isEmpty()) {
            throw new ApiException(
                    ErrorCode.USER_ALREADY_EXISTS,
                    "El email ya está registrado",
                    Map.of("email", req.getEmail()));
        }
        if (!usuarioRepository.findByUsername(req.getUsername()).isEmpty()) {
            throw new ApiException(
                    ErrorCode.USER_ALREADY_EXISTS,
                    "El nombre de usuario ya está en uso",
                    Map.of("username", req.getUsername()));
        }
        log.info("Registrando usuario: {}", req);
        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setApellidos(req.getApellidos());
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setEnabled(true);
        u.setAvatar(DEFAULT_AVATAR);
        log.info("Usuario creado: {}", u);

        Cartera cartera = new Cartera(u.getId(), u, 0.0);
        u.setCartera(cartera);
        log.info("Cartera creada: {}", cartera);
        u = usuarioRepository.save(u);

        AuthRequestDTO loginReq = new AuthRequestDTO(req.getEmail(), req.getPassword());
        return login(loginReq);
    }

    private AuthResponseDTO toResponse(UsuarioDetails ud, String token) {

        return new AuthResponseDTO(
                token,
                "Bearer",
                ud.getId(),
                ud.getEmail(),
                ud.getNombre(),
                ud.getApellidos(),
                ud.getUsername(),
                ud.getAvatar(),
                Instant.now().plusSeconds(3600));

    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getLogin(), req.getPassword()));

        UsuarioDetails ud = (UsuarioDetails) auth.getPrincipal();
        String token = jwtProvider.generateToken(ud);

        return toResponse(ud, token);
    }

    @Override
    public UsuarioDTO currentUser(String bearerToken) {

        String token = bearerToken.replaceFirst("^Bearer\\s+", "");

        if (!jwtProvider.validate(token)) {
            throw new ApiException(
                    ErrorCode.INVALID_TOKEN,
                    "Token no válido",
                    Map.of("token", token));
        }

        Long idUsuario = jwtProvider.getUserId(token);

        Usuario user = usuarioRepository.findById(idUsuario).get();

        return usuarioMapper.toDTO(user);
    }

}
