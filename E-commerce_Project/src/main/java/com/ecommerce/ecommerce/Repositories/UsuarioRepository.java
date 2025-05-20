package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Usuario;
// import org.springframework.data.jpa.repository.JpaRepository; // Ya no se importa directamente
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// CAMBIAR DE JpaRepository a BaseRepository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
    Optional<Usuario> findByUserName(String userName);
    Optional<Usuario> findByEmail(String email);
}