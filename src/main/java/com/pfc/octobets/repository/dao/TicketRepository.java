package com.pfc.octobets.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfc.octobets.repository.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

}
