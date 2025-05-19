package com.pfc.octobets.repository.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pfc.octobets.repository.entity.Apuesta;

@Repository
public interface ApuestaRepository extends JpaRepository<Apuesta, Long> {

    @EntityGraph(attributePaths = "opciones")
    @Query("SELECT a FROM Apuesta a WHERE a.estado = 'ABIERTA'")
    List<Apuesta> findAllAbiertas();

    @EntityGraph(attributePaths = "opciones")
    Optional<Apuesta> findById(Long id);
}
