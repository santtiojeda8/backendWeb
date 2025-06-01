package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Categoria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Importar Optional

@Repository
public interface CategoriaRepository extends BaseRepository<Categoria,Long> {
    List<Categoria> findByCategoriaPadreId(Long idPadre);
    List<Categoria> findByCategoriaPadreIsNull();

    // NUEVO: Método para buscar una categoría por su denominación
    Optional<Categoria> findByDenominacion(String denominacion);
}