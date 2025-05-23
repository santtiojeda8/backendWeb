package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Usuario;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
    // Busca un usuario por su nombre de usuario, solo si la cuenta está activa.
    Optional<Usuario> findByUserNameAndActivoTrue(String userName);

    // Busca un usuario por su email, solo si la cuenta está activa.
    Optional<Usuario> findByEmailAndActivoTrue(String email);

    // Busca un usuario por su nombre de usuario, independientemente de su estado 'activo'.
    // Útil para tareas administrativas o para verificar la existencia general de un usuario.
    Optional<Usuario> findByUserName(String userName);

    // Busca un usuario por su email, independientemente de su estado 'activo'.
    // Útil para tareas administrativas o para verificar si un email fue usado previamente
    // incluso si la cuenta está desactivada.
    Optional<Usuario> findByEmail(String email);

    // Cuenta el número de usuarios activos que tienen un email específico.
    // Muy útil para validar la unicidad del email al registrar nuevas cuentas
    // y asegurar que no haya colisiones con emails de cuentas activas.
    long countByEmailAndActivoTrue(String email);
}