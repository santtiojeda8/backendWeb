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
    public Categoria agregarSubcategoria(Long idCategoriaPadre, Categoria nuevaSubcategoria) throws Exception {
        try{
            // Obtener la categoría existente
            Categoria categoriaPadre = categoriaRepository.findById(idCategoriaPadre).orElse(null);

            if (categoriaPadre != null) {
                // Establecer la categoría existente como padre de la nueva subcategoría
                nuevaSubcategoria.setCategoriaPadre(categoriaPadre);
                //Creo la subCategoria como una nueva Categoria
                categoriaRepository.save(nuevaSubcategoria);

                /*// Agregar la nueva subcategoría a la lista de subcategorías de la categoría existente
                categoriaPadre.getSubcategorias().add(nuevaSubcategoria);
                // Guardar la categoría existente con la nueva subcategoría
                categoriaRepository.save(categoriaPadre);*/

                return nuevaSubcategoria;
            } else {
                // Manejar el caso en el que la categoría existente no se encuentre
                return null;
            }
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }
 /*//Método que liste las subcategorias de una categoria (id)
    @Transactional
    public List<Categoria> listarSubcategorias(Long idCategoriaPadre) throws Exception {
        try{
            // Obtener la categoría existente
            Categoria categoriaPadre = categoriaRepository.findById(idCategoriaPadre).orElse(null);

            if (categoriaPadre != null) {

                return (List<Categoria>) categoriaPadre.getSubcategorias();

            } else {
                // Manejar el caso en el que la categoría existente no se encuentre
                return null;
            }

        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }*/

    @Transactional
    public List<Categoria> listarPorCategoriaPadre(Long idCategoriaPadre) throws Exception {
        try {
            return categoriaRepository.findAllByCategoriaPadre_Id(idCategoriaPadre);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }


}
