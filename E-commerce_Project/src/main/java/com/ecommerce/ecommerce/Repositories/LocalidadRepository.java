// src/main/java/com/ecommerce/ecommerce/Repositories/LocalidadRepository.java
package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Entities.Provincia;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalidadRepository extends BaseRepository<Localidad, Long> {
    Optional<Localidad> findByNombreAndProvincia(String nombre, Provincia provincia);

    // Usa este método para la funcionalidad de "localidades por provincia"
    // Es el que te sugerí en el ejemplo de LocalidadService y LocalidadController
    List<Localidad> findByProvinciaId(Long provinciaId);
}