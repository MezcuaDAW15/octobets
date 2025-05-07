package com.pfc.octobets.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pfc.octobets.repository.entity.Opcion;

public interface OpcionRepository extends JpaRepository<Opcion, Long> {

    @Query("SELECT o FROM Opcion o WHERE o.idApuesta = idApuesta")
    List<Opcion> findAllByIdApuesta(Long idApuesta);

}
