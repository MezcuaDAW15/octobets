package com.pfc.octobets.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.model.dto.JuegoRequestDTO;
import com.pfc.octobets.model.dto.JuegoResponseDTO;
import com.pfc.octobets.model.mapper.JuegoMapper;
import com.pfc.octobets.model.mapper.UsuarioMapper;
import com.pfc.octobets.repository.dao.RondaRepository;
import com.pfc.octobets.repository.entity.Ronda;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de juego que implementa la lógica de cada tipo de partida.
 *
 * <p>
 * Responsabilidades:
 * <ul>
 * <li>Generar resultados de la partida.</li>
 * <li>Persistir cada ronda en Base de Datos.</li>
 * <li>Devolver al controlador la información necesaria para la UI.</li>
 * </ul>
 * </p>
 */
@Service
@Slf4j
public class JuegoServiceImpl implements JuegoService {

    private static final String[] SIMBOLOS_TRAGAPERRAS = { "CEREZA", "LIMÓN", "NARANJA", "SANDÍA", "ESTRELLA" };
    private static final int FILAS_TRAGAPERRAS = 3;
    private static final int COLUMNAS_TRAGAPERRAS = 5;

    private static final List<String> VALORES_CARTA = List.of("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J",
            "Q", "K");
    private static final List<String> PALOS_CARTA = List.of("CORAZONES", "PICAS", "ROMBOS", "TREBOLES");
    private static final int NIVELES_PLINKO = 8;
    private static final int BOTES_PLINKO = NIVELES_PLINKO + 1;
    private static final double[] MULTIPLICADORES_PLINKO = {
            0.5, 0.8, 1.0, 1.5, 2.0, 1.5, 1.0, 0.8, 0.5
    };

    @Autowired
    private RondaRepository rondaRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private JuegoMapper juegoMapper;

    @Autowired
    private CarteraService carteraService;

    /**
     * Ejecuta una partida de ruleta.
     *
     * @param dto datos de la partida: usuario, juego, apuesta y número elegido.
     * @return DTO con el resultado de la partida y datos para la animación en UI.
     * @throws IllegalArgumentException si el número elegido no está en [0,36].
     */
    @Override
    public JuegoResponseDTO jugarRuleta(JuegoRequestDTO dto) {
        log.info("Iniciando partida de ruleta para idUsuario={} con apuesta={}",
                dto.getUsuarioDTO().getId(), dto.getCantidadApostada());

        int elegido = ((Number) dto.getDatosJugada().get("numeroElegido")).intValue();
        int generado = ThreadLocalRandom.current().nextInt(37);

        boolean ganador = (elegido == generado);
        double cuota = 35.0;
        double ganancia = ganador ? dto.getCantidadApostada() * cuota : 0.0;

        // Persistir ronda
        Ronda ronda = new Ronda();
        ronda.setUsuario(usuarioMapper.toEntity(dto.getUsuarioDTO()));
        ronda.setJuego(juegoMapper.toEntity(dto.getJuegoDTO()));
        ronda.setCantidadFichas(dto.getCantidadApostada());
        ronda.setCuota(cuota);
        ronda.setEsGanadora(ganador);
        ronda.setFechaJugada(LocalDateTime.now());
        rondaRepository.save(ronda);

        // Cobrar
        carteraService.cobrar(dto.getUsuarioDTO().getId(), dto.getCantidadApostada());

        // Si es ganador, se paga la ganancia
        if (ganador) {
            carteraService.pagar(dto.getUsuarioDTO().getId(), ganancia);
        }

        // Preparar datos de respuesta
        Map<String, Object> infoVisual = new HashMap<>(2);
        infoVisual.put("numeroGanador", generado);
        infoVisual.put("numeroElegido", elegido);

        log.info("Ruleta finalizada: elegido={}, generado={}, ganador={}, ganancia={}",
                elegido, generado, ganador, ganancia);

        return new JuegoResponseDTO(ganador, (int) ganancia, infoVisual);
    }

    /**
     * Ejecuta una partida de tragaperras de 3 filas x 5 columnas.
     *
     * <p>
     * Reglas de pago:
     * <ul>
     * <li>Patrón "V": 5× apuesta.</li>
     * <li>Cada diagonal de 3 símbolos iguales: 3× apuesta.</li>
     * <li>Cada fila horizontal con ≥3 símbolos iguales: 2× apuesta.</li>
     * </ul>
     * </p>
     *
     * @param dto datos de la partida: usuario, juego y apuesta.
     * @return DTO con resultado de la partida y estado de la cuadrícula.
     */
    @Override
    public JuegoResponseDTO jugarTragaperras3x5(JuegoRequestDTO dto) {
        log.info("Iniciando tragaperras 3x5 para idUsuario={} con apuesta={}",
                dto.getUsuarioDTO().getId(), dto.getCantidadApostada());

        double apuesta = dto.getCantidadApostada();

        // 1. Generar cuadrícula aleatoria
        String[][] grid = generarGridAleatoria();
        log.debug("Grid generado: {}", Arrays.deepToString(grid));

        // 2. Contar horizontales de 3+ símbolos
        int countH = contarHorizontales(grid);

        // 3. Contar diagonales de 3 símbolos
        int countD = contarDiagonales(grid);

        // 4. Detectar patrón "V"
        boolean patronEnV = detectarPatronV(grid);

        // 5. Calcular cuota y tipo de victoria
        double cuota = calcularCuota(countH, countD, patronEnV);
        boolean ganador = (cuota > 0);
        String tipo = determinarTipoVictoria(countH, countD, patronEnV);
        double ganancia = apuesta * cuota;

        // Persistir ronda
        Ronda ronda = new Ronda();
        ronda.setUsuario(usuarioMapper.toEntity(dto.getUsuarioDTO()));
        ronda.setJuego(juegoMapper.toEntity(dto.getJuegoDTO()));
        ronda.setCantidadFichas(dto.getCantidadApostada());
        ronda.setCuota(cuota);
        ronda.setEsGanadora(ganador);
        ronda.setFechaJugada(LocalDateTime.now());
        // rondaRepository.save(ronda);

        // Cobrar
        carteraService.cobrar(dto.getUsuarioDTO().getId(), dto.getCantidadApostada());

        // Si es ganador, se paga la ganancia
        if (ganador) {
            carteraService.pagar(dto.getUsuarioDTO().getId(), ganancia);
        }

        // Preparar respuesta
        Map<String, Object> infoVisual = new HashMap<>(3);
        infoVisual.put("grid", grid);
        infoVisual.put("tipoVictoria", tipo);
        infoVisual.put("cuota", cuota);

        log.info("Tragaperras completada: tipoVictoria={}, cuota={}, ganancia={}",
                tipo, cuota, ganancia);

        return new JuegoResponseDTO(cuota > 0, (int) ganancia, infoVisual);
    }

    /**
     * Ejecuta una partida de Blackjack con lógica completa de HIT/​STAND.
     *
     * @param dto datos de la partida: usuario, juego, apuesta y estado actual.
     * @return DTO con resultado parcial o final y posibles acciones.
     */
    @Override
    public JuegoResponseDTO jugarBlackjack(JuegoRequestDTO dto) {
        long idUsuario = dto.getUsuarioDTO().getId();
        double cantidadApostada = dto.getCantidadApostada();
        log.info("Inicio Blackjack — IdUsuario={}, cantidadApostada={}", idUsuario, cantidadApostada);

        // Estado previo o nueva partida
        Map<String, Object> estado = dto.getDatosJugada();
        @SuppressWarnings("unchecked")
        List<String> manoJugador = estado.containsKey("manoJugador")
                ? new ArrayList<>((List<String>) estado.get("manoJugador"))
                : new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<String> manoCrupier = estado.containsKey("manoBanca")
                ? new ArrayList<>((List<String>) estado.get("manoBanca"))
                : new ArrayList<>();

        boolean gameOver = Boolean.TRUE.equals(estado.get("gameOver"));
        List<String> accionesPosibles = new ArrayList<>();
        int valorCrupier = -1;

        // Prepara el mazo completo
        List<String> mazoCompleto = VALORES_CARTA.stream()
                .flatMap(r -> PALOS_CARTA.stream().map(s -> r + "_" + s))
                .toList();

        // 1) Reparto inicial
        if (!estado.containsKey("manoJugador")) {
            // Al repartir, evita duplicados
            manoJugador.add(extraerCarta(mazoCompleto, manoJugador, manoCrupier));
            manoJugador.add(extraerCarta(mazoCompleto, manoJugador, manoCrupier));
            manoCrupier.add(extraerCarta(mazoCompleto, manoJugador, manoCrupier));
            manoCrupier.add(null); // segunda oculta
            accionesPosibles.addAll(List.of("HIT", "STAND"));
            log.debug("Reparto inicial — jugador={}, crupier=[{}, OCULTA]",
                    manoJugador, manoCrupier.get(0));

            // 2) Procesar acción HIT/STAND
        } else if (!gameOver) {
            String accion = ((String) estado.get("accion")).toUpperCase();
            log.debug("Acción '{}' para idUsuario={}", accion, idUsuario);

            switch (accion) {
                case "HIT" -> {
                    manoJugador.add(extraerCarta(mazoCompleto, manoJugador, manoCrupier));
                    int pVal = valorMano(manoJugador);
                    log.debug("El jugador pide carta → mano={}, valor={}", manoJugador, pVal);
                    if (pVal > 21) {
                        gameOver = true;
                        log.info("El jugador se pasa con {}", pVal);
                    } else {
                        accionesPosibles.addAll(List.of("HIT", "STAND"));
                    }
                }
                case "STAND" -> {
                    // Revelar carta oculta
                    if (manoCrupier.get(1) == null) {
                        manoCrupier.set(1, extraerCarta(mazoCompleto, manoJugador, manoCrupier));
                    }
                    valorCrupier = jugarCrupier(mazoCompleto, manoJugador, manoCrupier);
                    gameOver = true;
                    log.debug("Mano final del crupier={} → valor={}", manoCrupier, valorCrupier);
                }
                default -> throw new IllegalArgumentException("Acción inválida: " + accion);
            }
        }

        // 3) Resolución final
        boolean victoria = false, empate = false;
        double multiplier = 0.0, ganancia = 0.0;
        if (gameOver) {
            // Asegurar revelado
            if (manoCrupier.get(1) == null) {
                manoCrupier.set(1, extraerCarta(mazoCompleto, manoJugador, manoCrupier));
                log.debug("El crupier revela forzosamente {}", manoCrupier.get(1));
            }
            int pVal = valorMano(manoJugador);
            if (valorCrupier < 0) {
                valorCrupier = valorMano(manoCrupier);
            }
            boolean natural = (pVal == 21 && manoJugador.size() == 2);
            victoria = natural || (pVal <= 21 && (valorCrupier > 21 || pVal > valorCrupier));
            empate = !victoria && (pVal == valorCrupier) && pVal <= 21;

            multiplier = natural ? 2.5
                    : victoria ? 2.0
                            : empate ? 1.0
                                    : 0.0;
            ganancia = cantidadApostada * multiplier;

            log.info("Resultado — pVal={}, dVal={}, natural={}, victoria={}, empate={}, ganancia={}",
                    pVal, valorCrupier, natural, victoria, empate, ganancia);

            // Persistir ronda
            Ronda ronda = new Ronda();
            ronda.setUsuario(usuarioMapper.toEntity(dto.getUsuarioDTO()));
            ronda.setJuego(juegoMapper.toEntity(dto.getJuegoDTO()));
            ronda.setCantidadFichas(cantidadApostada);
            ronda.setCuota(multiplier);
            ronda.setEsGanadora(victoria);
            ronda.setFechaJugada(LocalDateTime.now());
            rondaRepository.save(ronda);

            // Cobrar
            carteraService.cobrar(dto.getUsuarioDTO().getId(), dto.getCantidadApostada());

            // Si es ganador, se paga la ganancia
            if (victoria) {
                carteraService.pagar(dto.getUsuarioDTO().getId(), ganancia);
            }

        }

        // 4) Responder al front
        Map<String, Object> infoVisual = new HashMap<>();
        infoVisual.put("manoJugador", manoJugador);
        infoVisual.put("manoBanca", manoCrupier);
        infoVisual.put("gameOver", gameOver);
        infoVisual.put("accionesPosibles", gameOver ? List.of() : accionesPosibles);
        infoVisual.put("cuota", multiplier);
        if (gameOver) {
            infoVisual.put("fichasGanadas", (int) ganancia);
            infoVisual.put("resultado", victoria ? "GANADOR" : empate ? "EMPATE" : "DERROTA");
        }

        return new JuegoResponseDTO(victoria, (int) ganancia, infoVisual);
    }

    /**
     * Ejecuta una partida de Plinko:
     * - Genera un camino de NIVELES_PLINKO clavijas (true = derecha, false =
     * izquierda).
     * - Calcula la casilla final partiendo del centro.
     * - Asigna una cuota según la casilla y calcula la ganancia.
     * - Persiste la ronda y devuelve los datos necesarios para animar en el
     * frontend.
     */
    @Override
    public JuegoResponseDTO jugarPlinko(JuegoRequestDTO dto) {
        long usuarioId = dto.getUsuarioDTO().getId();
        double apuesta = dto.getCantidadApostada();
        log.info("PLINKO: inicio de partida — usuarioId={}, apuesta={}", usuarioId, apuesta);

        // 1) Generar camino aleatorio de la bola
        boolean[] camino = new boolean[NIVELES_PLINKO];
        for (int nivel = 0; nivel < NIVELES_PLINKO; nivel++) {
            camino[nivel] = ThreadLocalRandom.current().nextBoolean();
        }
        log.debug("PLINKO: camino generado = {}", Arrays.toString(camino));

        // 2) Calcular casilla final, partiendo siempre de la posición central
        int casilla = NIVELES_PLINKO / 2;
        for (boolean derecha : camino) {
            casilla += derecha ? 1 : -1;
            // Asegurar que casilla esté en [0, BOTES_PLINKO-1]
            casilla = Math.max(0, Math.min(BOTES_PLINKO - 1, casilla));
        }
        log.debug("PLINKO: casilla final = {}", casilla);

        // 3) Determinar cuota y ganancia
        double cuota = MULTIPLICADORES_PLINKO[casilla];
        double ganancia = apuesta * cuota;
        boolean ganador = cuota > 0;
        log.info("PLINKO: resultado — cuota={}, ganancia={}, ganador={}",
                cuota, ganancia, ganador);

        // Persistir ronda
        Ronda ronda = new Ronda();
        ronda.setUsuario(usuarioMapper.toEntity(dto.getUsuarioDTO()));
        ronda.setJuego(juegoMapper.toEntity(dto.getJuegoDTO()));
        ronda.setCantidadFichas(apuesta);
        ronda.setCuota(cuota);
        ronda.setEsGanadora(ganador);
        ronda.setFechaJugada(LocalDateTime.now());
        // rondaRepository.save(ronda);

        // Cobrar
        carteraService.cobrar(dto.getUsuarioDTO().getId(), dto.getCantidadApostada());

        // Si es ganador, se paga la ganancia
        if (ganador) {
            carteraService.pagar(dto.getUsuarioDTO().getId(), ganancia);
        }

        // 4) Preparar infoVisual para el frontend
        List<Boolean> listaCamino = IntStream.range(0, camino.length)
                .mapToObj(i -> camino[i])
                .collect(Collectors.toList());

        Map<String, Object> infoVisual = new HashMap<>();
        infoVisual.put("camino", listaCamino);
        infoVisual.put("casillaFinal", casilla);
        infoVisual.put("cuota", cuota);

        return new JuegoResponseDTO(ganador, (int) ganancia, infoVisual);
    }

    // ================== Helpers privados ==================

    private String[][] generarGridAleatoria() {
        String[][] grid = new String[FILAS_TRAGAPERRAS][COLUMNAS_TRAGAPERRAS];
        for (int r = 0; r < FILAS_TRAGAPERRAS; r++) {
            for (int c = 0; c < COLUMNAS_TRAGAPERRAS; c++) {
                grid[r][c] = SIMBOLOS_TRAGAPERRAS[ThreadLocalRandom.current().nextInt(SIMBOLOS_TRAGAPERRAS.length)];
            }
        }
        return grid;
    }

    private int contarHorizontales(String[][] grid) {
        int count = 0;
        for (int r = 0; r < FILAS_TRAGAPERRAS; r++) {
            for (int c = 0; c <= COLUMNAS_TRAGAPERRAS - 3; c++) {
                String s = grid[r][c];
                if (s.equals(grid[r][c + 1]) && s.equals(grid[r][c + 2])) {
                    count++;
                    break; // una por fila
                }
            }
        }
        return count;
    }

    private int contarDiagonales(String[][] grid) {
        int count = 0;
        for (int c = 0; c <= COLUMNAS_TRAGAPERRAS - 3; c++) {
            if (grid[0][c].equals(grid[1][c + 1]) && grid[0][c].equals(grid[2][c + 2]))
                count++;
            if (grid[2][c].equals(grid[1][c + 1]) && grid[2][c].equals(grid[0][c + 2]))
                count++;
        }
        return count;
    }

    private boolean detectarPatronV(String[][] grid) {
        String centro = grid[2][2];
        return centro.equals(grid[1][1]) &&
                centro.equals(grid[0][0]) &&
                centro.equals(grid[1][3]) &&
                centro.equals(grid[0][4]);
    }

    private double calcularCuota(int h, int d, boolean v) {
        if (v)
            return 5.0;
        if (d > 0)
            return 3.0 * d;
        if (h > 0)
            return 2.0 * h;
        return 0.0;
    }

    private String determinarTipoVictoria(int h, int d, boolean v) {
        if (v)
            return "V";
        else if (d > 0)
            return "DIAGONAL x" + d;
        else if (h > 0)
            return "HORIZONTAL x" + h;
        else
            return "NINGUNA";
    }

    /**
     * Extrae una carta aleatoria del mazo completo,
     * excluyendo las ya presentes en manoJugador y manoCrupier.
     */
    private String extraerCarta(List<String> mazoCompleto,
            List<String> manoJugador,
            List<String> manoCrupier) {
        Set<String> used = new HashSet<>();
        used.addAll(manoJugador);
        manoCrupier.stream().filter(Objects::nonNull).forEach(used::add);

        List<String> remaining = mazoCompleto.stream()
                .filter(c -> !used.contains(c))
                .toList();

        if (remaining.isEmpty()) {
            throw new IllegalStateException("No quedan cartas disponibles en el mazo");
        }

        return remaining.get(ThreadLocalRandom.current().nextInt(remaining.size()));
    }

    /**
     * Igual que jugarCrupier, pero usando extraerCarta para no duplicar cartas
     * ya repartidas a manoJugador y manoCrupier.
     */
    private int jugarCrupier(List<String> mazoCompleto,
            List<String> manoJugador,
            List<String> manoCrupier) {
        int value = valorMano(manoCrupier);
        while (value < 17) {
            String card = extraerCarta(mazoCompleto, manoJugador, manoCrupier);
            manoCrupier.add(card);
            value = valorMano(manoCrupier);
            log.debug("El crupier roba {} → valor={}", card, value);
        }
        return value;
    }

    // Valor óptimo de la mano (As = 1 o 11)
    private int valorMano(List<String> hand) {
        int sum = 0, aces = 0;
        for (String card : hand) {
            if (card == null)
                continue;
            String rank = card.split("_")[0];
            switch (rank) {
                case "J", "Q", "K" -> sum += 10;
                case "A" -> aces++;
                default -> sum += Integer.parseInt(rank);
            }
        }
        sum += aces; // Ases como 1
        if (aces > 0 && sum + 10 <= 21)
            sum += 10; // uno como 11
        return sum;
    }

}
