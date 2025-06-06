// com.ecommerce.ecommerce.Repositories.LocalidadRepository.java
package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Entities.Provincia;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalidadRepository extends BaseRepository<Localidad, Long> {
    // Método que necesitas para buscar localidades activas por nombre y provincia
    Optional<Localidad> findByNombreAndProvinciaAndActivoTrue(String nombre, Provincia provincia);

    // Método que necesitas para buscar localidad activa por ID
    Optional<Localidad> findByIdAndActivoTrue(Long id);

    // Puedes mantener este si lo usas para otras funcionalidades (ej. dropdowns en UI)
    List<Localidad> findByProvinciaId(Long provinciaId);

    // Si también necesitas buscar sin importar el estado 'activo', mantén este:
    // Optional<Localidad> findByNombreAndProvincia(String nombre, Provincia provincia);
}