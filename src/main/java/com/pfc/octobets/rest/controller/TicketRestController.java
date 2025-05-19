package com.pfc.octobets.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfc.octobets.model.dto.TicketDTO;
import com.pfc.octobets.service.TicketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ws/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketRestController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public TicketDTO crearTicket(@RequestBody TicketDTO ticketDTO) {
        log.info("Petición recibida: crear ticket.");
        return ticketService.crearTicket(ticketDTO);
    }

}
