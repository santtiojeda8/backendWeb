package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Categoria;
import com.ecommerce.ecommerce.Repositories.CategoriaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService implements BaseService<Categoria>{
    @Autowired
    private CategoriaRepositorio categoryRepository;

    @Override
    public List<Categoria> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Categoria finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Categoria save(Categoria entity) throws Exception {
        return null;
    }

    @Override
    public Categoria update(Long id, Categoria newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
