package com.pfc.octobets.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfc.octobets.repository.entity.Juego;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

}
