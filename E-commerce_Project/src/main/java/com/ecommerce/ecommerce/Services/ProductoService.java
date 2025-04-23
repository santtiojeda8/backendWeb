package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Repositories.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements BaseService<Producto>{

    @Autowired
    private ProductoRepositorio productRepository;

    @Override
    public List<Producto> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Producto finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Producto save(Producto entity) throws Exception {
        return null;
    }

    @Override
    public Producto update(Long id, Producto newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
