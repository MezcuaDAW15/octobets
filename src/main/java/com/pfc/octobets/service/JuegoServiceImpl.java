package com.pfc.octobets.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.JuegoRequestDTO;
import com.pfc.octobets.model.dto.JuegoResponseDTO;
import com.pfc.octobets.model.mapper.JuegoMapper;
import com.pfc.octobets.model.mapper.UsuarioMapper;
import com.pfc.octobets.repository.dao.RondaRepository;
import com.pfc.octobets.repository.entity.Ronda;

@Service
public class JuegoServiceImpl implements JuegoService {

    @Autowired
    private RondaRepository rondaRepository;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private JuegoMapper juegoMapper;

    @Override
    public JuegoResponseDTO jugarRuleta(JuegoRequestDTO dto) {
        int numeroElegido = (int) dto.getDatosJugada().get("numeroElegido");
        int numeroGanador = (int) (Math.random() * 37); // Genera un número entero entre 0 y 36

        boolean gana = numeroElegido == numeroGanador;
        double cuota = 35.0;
        double fichasGanadas = gana ? dto.getCantidadApostada() * cuota : 0;

        Ronda ronda = new Ronda();
        ronda.setUsuario(usuarioMapper.toEntity(dto.getUsuarioDTO()));
        ronda.setJuego(juegoMapper.toEntity(dto.getJuegoDTO()));
        ronda.setCantidadFichas(dto.getCantidadApostada());
        ronda.setCuota(cuota);
        ronda.setEsGanadora(gana);
        ronda.setFechaJugada(LocalDateTime.now());

        rondaRepository.save(ronda);

        // Modificación de cartera

        Map<String, Object> infoVisual = new HashMap<>();
        infoVisual.put("numeroGanador", numeroGanador);
        infoVisual.put("numeroElegido", numeroElegido);

        return new JuegoResponseDTO(gana, (int) fichasGanadas, infoVisual);
    }
}
