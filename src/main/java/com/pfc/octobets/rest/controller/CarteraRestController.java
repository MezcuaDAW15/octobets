package com.pfc.octobets.rest.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.config.StripeConfig;
import com.pfc.octobets.model.dto.CarteraDTO;
import com.pfc.octobets.security.UsuarioDetails;
import com.pfc.octobets.service.CarteraService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ws/carteras")
@RequiredArgsConstructor
@Slf4j
public class CarteraRestController {

    @Autowired
    private CarteraService carteraService;

    @Autowired
    private StripeConfig stripeConfig;

    @GetMapping("/{idUsuario}")
    public ResponseEntity<CarteraDTO> getCarteraByUsuario(@PathVariable Long idUsuario) {
        log.info("Petición recibida: obtener cartera por usuario id={}", idUsuario);
        CarteraDTO cartera = carteraService.findByUsuario(idUsuario);
        if (cartera != null) {
            log.info("Cartera encontrada: {}", cartera);
            return ResponseEntity.ok(cartera);
        } else {
            log.warn("No se encontró la cartera para el usuario id={}", idUsuario);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/buy")
    public Map<String, String> buy(
            @RequestParam double chips,
            @AuthenticationPrincipal UsuarioDetails user) {

        PaymentIntent intent = carteraService.createBuyIntent(user.getId(), chips);
        return Map.of("clientSecret", intent.getClientSecret());
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<Void> handleWebhook(HttpServletRequest request, @RequestBody String payload)
            throws SignatureVerificationException, IOException, StripeException {

        String sigHeader = request.getHeader("Stripe-Signature");
        Event event = Webhook.constructEvent(
                payload, sigHeader, stripeConfig.getWebhookSecret());

        if ("payment_intent.succeeded".equals(event.getType())) {
            log.info("Evento de Stripe recibido: {}", event.getType());
            PaymentIntent pi = (PaymentIntent) event.getData().getObject();
            carteraService.confirmBuy(pi.getId()); // ✅ acredita fichas
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestParam double chips, Principal me) {
        carteraService.withdrawChips(Long.valueOf(me.getName()), chips);
    }

    @GetMapping("/balance")
    public double balance(Authentication authentication) {

        // 1) Obtén tu entidad de usuario
        UsuarioDetails user = (UsuarioDetails) authentication.getPrincipal();
        Long idUsuario = user.getId(); // el ID numérico de tu tabla usuarios

        // 2) Usa el servicio
        return carteraService.getBalance(idUsuario);
    }
}
