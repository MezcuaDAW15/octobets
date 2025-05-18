package com.pfc.octobets.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pfc.octobets.repository.entity.Opcion;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Long> {

    @Query("SELECT o FROM Opcion o WHERE o.apuesta.id = :idApuesta")
    List<Opcion> findAllByIdApuesta(Long idApuesta);

}
