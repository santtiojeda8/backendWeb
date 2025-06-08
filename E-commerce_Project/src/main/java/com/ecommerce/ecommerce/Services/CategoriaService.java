package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Categoria;
import com.ecommerce.ecommerce.Repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Use Spring's Transactional

import java.util.List;

@Service
public class CategoriaService extends BaseService<Categoria, Long> {

    // No necesitas @Autowired aquí ni un campo privado para categoriaRepository
    // Ya recibes CategoriaRepository en el constructor y se asigna a 'baseRepository'
    // en la clase BaseService.

    public CategoriaService(CategoriaRepository categoriaRepository) {
        super(categoriaRepository);
        // 'this.baseRepository' now holds the CategoriaRepository instance
        // You can cast it if you need CategoriaRepository specific methods
        // but it's already type-safe for methods defined in CategoriaRepository
        // because BaseRepository<Categoria, Long> is implemented by CategoriaRepository.
    }

    @Transactional(readOnly = true) // Always specify readOnly for read operations
    public List<Categoria> listarSubcategorias(Long idPadre) {
        // Cast baseRepository to CategoriaRepository to access specific methods
        // Or simply use baseRepository if the method signature allows (it does here as CategoriaRepository extends BaseRepository)
        return ((CategoriaRepository) baseRepository).findByCategoriaPadreId(idPadre);
    }

    @Transactional(readOnly = true) // Always specify readOnly for read operations
    public List<Categoria> listarCategoriasRaiz() {
        // Cast baseRepository to CategoriaRepository to access specific methods
        return ((CategoriaRepository) baseRepository).findByCategoriaPadreIsNull();
    }
}