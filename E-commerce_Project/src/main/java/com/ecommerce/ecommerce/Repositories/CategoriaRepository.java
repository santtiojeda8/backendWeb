package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Categoria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends BaseRepository<Categoria, Long> {

    // Buscar todas las subcategorías de una categoría padre dada
    List<Categoria> findAllByCategoriaPadre_Id(Long idCategoriaPadre);

}
