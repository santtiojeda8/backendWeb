package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Provincia;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importar Optional

@Repository
public interface ProvinciaRepository extends BaseRepository<Provincia,Long>{
    // Nuevo método para buscar provincia por nombre
    Optional<Provincia> findByNombre(String nombre);
}