package com.pfc.octobets.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfc.octobets.repository.entity.Transaccion;

public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

}
