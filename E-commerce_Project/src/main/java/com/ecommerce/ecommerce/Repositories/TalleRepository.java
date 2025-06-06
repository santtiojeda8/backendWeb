package com.ecommerce.ecommerce.Repositories;
import com.ecommerce.ecommerce.Entities.Talle;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TalleRepository extends BaseRepository<Talle, Long> {
    // Puedes añadir métodos específicos si necesitas buscar talles por nombre, etc.
    Optional<Talle> findByNombreTalleAndActivoTrue(String nombreTalle);
}
