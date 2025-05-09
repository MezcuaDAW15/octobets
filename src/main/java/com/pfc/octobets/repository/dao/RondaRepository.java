package com.pfc.octobets.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfc.octobets.repository.entity.Ronda;

@Repository
public interface RondaRepository extends JpaRepository<Ronda, Long> {

}
