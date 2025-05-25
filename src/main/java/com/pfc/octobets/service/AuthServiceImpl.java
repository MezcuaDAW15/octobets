package com.pfc.octobets.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.AuthRequestDTO;
import com.pfc.octobets.model.dto.AuthResponseDTO;
import com.pfc.octobets.model.dto.RegisterRequestDTO;
import com.pfc.octobets.model.dto.UsuarioDTO;
import com.pfc.octobets.model.mapper.UsuarioMapper;
import com.pfc.octobets.repository.dao.CarteraRepository;
import com.pfc.octobets.repository.dao.UsuarioRepository;
import com.pfc.octobets.repository.entity.Cartera;
import com.pfc.octobets.repository.entity.Usuario;
import com.pfc.octobets.security.JwtProvider;
import com.pfc.octobets.security.UsuarioDetails;

import io.jsonwebtoken.JwtException;
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
        // 1) crear usuario + cartera
        if (!usuarioRepository.findByEmail(req.getEmail()).isEmpty()) {
            throw new IllegalArgumentException("El email ya está registrado");

        }
        if (!usuarioRepository.findByUsername(req.getUsername()).isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
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

        // 2) Autenticar automáticamente
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

        // 1) quitar prefijo "Bearer "
        String token = bearerToken.replaceFirst("^Bearer\\s+", "");

        // 2) validar y extraer email (sub)
        if (!jwtProvider.validate(token))
            throw new JwtException("Token no válido");

        Long idUsuario = jwtProvider.getUserId(token);

        // 3) buscar usuario y mapear a DTO
        Usuario user = usuarioRepository.findById(idUsuario).get();

        return usuarioMapper.toDTO(user);
    }

}
