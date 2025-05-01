package com.pfc.octobets.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfc.octobets.repository.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
