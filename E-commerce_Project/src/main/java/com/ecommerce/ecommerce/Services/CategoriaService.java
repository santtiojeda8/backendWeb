package com.ecommerce.ecommerce.Services;



import com.ecommerce.ecommerce.Entities.Categoria;
import com.ecommerce.ecommerce.Repositories.CategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService extends BaseService<Categoria, Long> {

    public CategoriaService(CategoriaRepository categoriaRepository) {
        super(categoriaRepository);
    }

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Transactional
    public List<Categoria> listarSubcategorias(Long idPadre) {
        return categoriaRepository.findByCategoriaPadreId(idPadre);
    }

    @Transactional
    public List<Categoria> listarCategoriasRaiz() {
        return categoriaRepository.findByCategoriaPadreIsNull();
    }


}
