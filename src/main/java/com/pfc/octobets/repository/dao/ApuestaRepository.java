package com.pfc.octobets.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfc.octobets.repository.entity.Apuesta;

@Repository
public interface ApuestaRepository extends JpaRepository<Apuesta, Long> {

    @EntityGraph(attributePaths = "opciones")
    List<Apuesta> findAll();
}
