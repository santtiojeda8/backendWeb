package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Color;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends BaseRepository<Color, Long> {
    // Puedes añadir métodos específicos si necesitas buscar colores por nombre, etc.
    Optional<Color> findByNombreColorAndActivoTrue(String nombreColor);
}
