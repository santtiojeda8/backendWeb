package com.ecommerce.ecommerce.Services;


import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Repositories.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService extends BaseService<Producto, Long> {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        super(productoRepository);
        this.productoRepository = productoRepository;
    }

    public List<Producto> findAllByCategoriaId(Long categoriaId) throws Exception {
       try{
           return productoRepository.findAllByCategoriaId(categoriaId);
       }catch (Exception e){
           throw new Exception(e.getMessage());
       }
    }
    @Transactional
    public List<Producto> findProductosConPromocion() throws Exception  {
        try{
            return productoRepository.findAll().stream()
                    .filter(Producto::isTienePromocion)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public List<Producto> buscarPorNombre(String keyword)  throws Exception {
        try{
            return productoRepository.findByDenominacionContainingIgnoreCase(keyword);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

}
