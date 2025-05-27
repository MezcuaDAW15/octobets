package com.pfc.octobets.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.common.ApiException;
import com.pfc.octobets.common.ErrorCode;
import com.pfc.octobets.model.dto.CarteraDTO;
import com.pfc.octobets.model.dto.TransaccionDTO;
import com.pfc.octobets.model.mapper.CarteraMapper;
import com.pfc.octobets.model.mapper.TransaccionMapper;
import com.pfc.octobets.repository.dao.CarteraRepository;
import com.pfc.octobets.repository.dao.TransaccionRepository;
import com.pfc.octobets.repository.entity.Cartera;
import com.pfc.octobets.repository.entity.Transaccion;
import com.pfc.octobets.repository.entity.Transaccion.Estado;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CarteraServiceImpl implements CarteraService {

    private static final double EUR_TO_CHIPS = 100.0;

    @Autowired
    private CarteraRepository carteraRepository;

    @Autowired
    private CarteraMapper carteraMapper;

    @Autowired
    private TransaccionMapper transaccionMapper;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Override
    public Double getSaldo(Long idUsuario) {
        log.info("Búsqueda del saldo de la cartera del usuario con id={}", idUsuario);
        return carteraRepository.findById(idUsuario)
                .map(cartera -> cartera.getSaldoFichas())
                .orElseThrow(() -> new ApiException(
                        ErrorCode.USER_NOT_FOUND,
                        "Cartera no encontrada para el usuario",
                        Map.of("id", idUsuario)));
    }

    public CarteraDTO findByUsuario(Long idUsuario) {
        log.info("Búsqueda de la cartera del usuario con id={}", idUsuario);
        return carteraRepository.findById(idUsuario)
                .map(carteraMapper::toDTO)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.USER_NOT_FOUND,
                        "Cartera no encontrada para el usuario",
                        Map.of("id", idUsuario)));
    }

    @Override
    public void cobrar(Long id, Double cantidadApostada) {
        log.info("Cobro de {} a la cartera con id={}", cantidadApostada, id);
        carteraRepository.findById(id)
                .ifPresentOrElse(cartera -> {
                    double nuevoSaldo = cartera.getSaldoFichas() - cantidadApostada;
                    cartera.setSaldoFichas(nuevoSaldo);
                    carteraRepository.save(cartera);
                    log.info("Nuevo saldo de la cartera con id={} es {}", id, nuevoSaldo);
                }, () -> {
                    throw new ApiException(
                            ErrorCode.USER_NOT_FOUND,
                            "Cartera no encontrada para el usuario",
                            Map.of("id", id));
                });
    }

    @Override
    public PaymentIntent createBuyIntent(Long userId, double chips) {
        log.info("Creando PaymentIntent para usuario id={} con chips={}", userId, chips);
        Cartera cartera = carteraRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND, "Cartera no existe"));

        long amountCents = Math.round((chips / EUR_TO_CHIPS) * 100); // 100 fichas = 1 €

        Map<String, Object> params = Map.of(
                "amount", amountCents,
                "currency", "eur",
                "automatic_payment_methods", Map.of("enabled", true), // 👈 NUEVO
                "metadata", Map.of(
                        "userId", userId, // 👈 usa SIEMPRE las mismas keys
                        "chips", chips));

        try {
            PaymentIntent intent = PaymentIntent.create(params);

            Transaccion tx = new Transaccion();
            tx.setCantidadDinero(amountCents / 100.0);
            tx.setCantidadFichas(chips);
            tx.setFecha(LocalDateTime.now());
            tx.setTipo(Transaccion.Tipo.DEPOSITO);
            tx.setStripeId(intent.getId());
            tx.setEstado(Estado.PENDING);
            tx.setCartera(cartera);
            transaccionRepository.save(tx);

            return intent;

        } catch (StripeException e) {
            log.error("Stripe dijo NO: {}", e.getMessage(), e);
            throw new ApiException(ErrorCode.STRIPE_PAYMENT_FAILED,
                    "Stripe no aceptó el pago");
        }
    }

    @Override
    public void confirmBuy(String paymentIntentId) throws StripeException {
        log.info("Confirmando compra con PaymentIntent id={}", paymentIntentId);
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

        long userId = Long.parseLong(intent.getMetadata().get("userId"));
        double chips = Double.parseDouble(intent.getMetadata().get("chips"));

        Cartera cartera = carteraRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND, "Cartera no existe"));

        cartera.setSaldoFichas(cartera.getSaldoFichas() + chips);
        carteraRepository.save(cartera);

        // marca la transacción como SUCCEEDED (si añadiste el campo estado)
        transaccionRepository.findByStripeId(paymentIntentId)
                .ifPresent(tx -> {
                    tx.setEstado(Estado.SUCCEEDED);
                    transaccionRepository.save(tx);
                });
    }

    @Override
    @Transactional
    public void withdrawChips(Long userId, double chips) {
        Cartera cartera = carteraRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND, "Cartera no existe"));

        if (cartera.getSaldoFichas() < chips) {
            throw new ApiException(ErrorCode.INSUFFICIENT_CHIPS, "Saldo insuficiente");
        }

        cartera.setSaldoFichas(cartera.getSaldoFichas() - chips);
        carteraRepository.save(cartera);

        Transaccion tx = new Transaccion();
        tx.setCantidadDinero(-chips / EUR_TO_CHIPS);
        tx.setCantidadFichas(-chips);
        tx.setFecha(LocalDateTime.now());
        tx.setTipo(Transaccion.Tipo.RETIRO);
        tx.setStripeId(null);
        tx.setEstado(Transaccion.Estado.SUCCEEDED);
        tx.setCartera(cartera);

        transaccionRepository.save(tx);
    }

    @Override
    public double getBalance(Long userId) {
        Cartera cartera = carteraRepository.findById(userId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.WALLET_NOT_FOUND,
                        "Cartera no existe",
                        Map.of("userId", userId)));
        return cartera.getSaldoFichas();
    }

    @Override
    public List<TransaccionDTO> listTransactions(Long userId) {
        Cartera cartera = carteraRepository.findById(userId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.WALLET_NOT_FOUND,
                        "Cartera no existe",
                        Map.of("userId", userId)));
        return transaccionRepository.findAllByCarteraId(cartera.getIdUsuario())
                .stream()
                .map(transaccionMapper::toDTO)
                .toList();
    }

}