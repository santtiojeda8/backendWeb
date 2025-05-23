package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Base; // <--- Importa tu clase Base
import org.springframework.data.domain.Page; // <--- Importa Page
import org.springframework.data.domain.Pageable; // <--- Importa Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
// Asegúrate de que E extienda Base para poder usar el campo 'activo'
public interface BaseRepository<E extends Base, ID extends Serializable> extends JpaRepository<E, ID> {

    // Nuevos métodos para filtrar por 'activo = true'
    // Spring Data JPA generará automáticamente la consulta SQL para estos nombres de método
    List<E> findAllByActivoTrue(); // Obtener todos los activos
    Page<E> findAllByActivoTrue(Pageable pageable); // Obtener activos paginados
    Optional<E> findByIdAndActivoTrue(ID id); // Obtener uno por ID, solo si está activo
}