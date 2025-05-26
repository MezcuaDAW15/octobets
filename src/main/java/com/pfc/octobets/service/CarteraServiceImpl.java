package com.pfc.octobets.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfc.octobets.common.ApiException;
import com.pfc.octobets.common.ErrorCode;
import com.pfc.octobets.model.dto.CarteraDTO;
import com.pfc.octobets.model.mapper.CarteraMapper;
import com.pfc.octobets.repository.dao.CarteraRepository;
import com.pfc.octobets.repository.dao.TransaccionRepository;
import com.pfc.octobets.repository.entity.Cartera;
import com.pfc.octobets.repository.entity.Transaccion;
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
    public PaymentIntent createBuyIntent(Long idUsuario, double chips) {
        log.info("Creando PaymentIntent para usuario id={} con chips={}", idUsuario, chips);
        Cartera cartera = carteraRepository.findById(idUsuario)
                .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND, "Cartera no existe"));

        long amountCents = Math.round((chips / EUR_TO_CHIPS) * 100);

        Map<String, Object> params = Map.of(
                "amount", amountCents,
                "currency", "eur",
                "metadata", Map.of("idUsuario", idUsuario, "chips", chips));

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            // guardamos transacción en estado 'pendiente'
            Transaccion tx = new Transaccion(null, amountCents / 100.0, chips,
                    LocalDateTime.now(), Transaccion.Tipo.DEPOSITO,
                    intent.getId(), cartera);
            transaccionRepository.save(tx);
            return intent;
        } catch (StripeException e) {
            throw new ApiException(ErrorCode.STRIPE_PAYMENT_FAILED, "Stripe no aceptó el pago");
        }
    }

    @Override
    public void confirmBuy(String paymentIntentId) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        long userId = Long.parseLong(intent.getMetadata().get("userId"));
        double chips = Double.parseDouble(intent.getMetadata().get("chips"));

        Cartera cartera = carteraRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND, "Cartera no existe"));

        cartera.setSaldoFichas(cartera.getSaldoFichas() + chips);
        carteraRepository.save(cartera);
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

        Transaccion tx = new Transaccion(null,
                -chips / EUR_TO_CHIPS, // dinero ficticio negativo
                -chips,
                LocalDateTime.now(),
                Transaccion.Tipo.RETIRO,
                null, // sin Stripe real
                cartera);
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

}