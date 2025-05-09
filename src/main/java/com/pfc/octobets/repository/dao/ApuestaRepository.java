package com.pfc.octobets.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfc.octobets.repository.entity.Apuesta;

@Repository
public interface ApuestaRepository extends JpaRepository<Apuesta, Long> {

}
