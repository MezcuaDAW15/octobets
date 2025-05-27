package com.pfc.octobets.repository.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pfc.octobets.repository.entity.Transaccion;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    @Query("SELECT t FROM Transaccion t WHERE t.stripeId = ?1")
    Optional<Transaccion> findByStripeId(String paymentIntentId);

}
