// com.ecommerce.ecommerce.Repositories.ProvinciaRepository.java
package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Provincia;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinciaRepository extends BaseRepository<Provincia,Long>{ // Asumo que BaseRepository existe
    // Método que necesitas para buscar provincias activas por nombre
    Optional<Provincia> findByNombreAndActivoTrue(String nombre);

    // Si también necesitas buscar sin importar el estado 'activo', mantén este:
    // Optional<Provincia> findByNombre(String nombre);
}