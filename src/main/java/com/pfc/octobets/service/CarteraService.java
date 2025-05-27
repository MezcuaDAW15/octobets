package com.pfc.octobets.service;

import java.util.List;

import com.pfc.octobets.model.dto.CarteraDTO;
import com.pfc.octobets.model.dto.TransaccionDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface CarteraService {

    Double getSaldo(Long idUsuario);

    void cobrar(Long id, Double cantidadApostada);

    CarteraDTO findByUsuario(Long idUsuario);

    PaymentIntent createBuyIntent(Long valueOf, double chips);

    void confirmBuy(String paymentIntentId) throws StripeException;

    void withdrawChips(Long idUsuario, double chips);

    double getBalance(Long idUsuario);

    List<TransaccionDTO> listTransactions(Long userId);

}