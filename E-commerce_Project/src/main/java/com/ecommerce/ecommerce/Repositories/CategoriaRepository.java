package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Categoria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends BaseRepository<Categoria,Long> {
    List<Categoria> findByCategoriaPadreId(Long idPadre);
    List<Categoria> findByCategoriaPadreIsNull();

}
