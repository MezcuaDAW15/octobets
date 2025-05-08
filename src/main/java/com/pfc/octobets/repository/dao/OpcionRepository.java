package com.pfc.octobets.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfc.octobets.repository.entity.Opcion;

public interface OpcionRepository extends JpaRepository<Opcion, Long> {

    List<Opcion> findAllByApuestaId(Long idApuesta);

}
