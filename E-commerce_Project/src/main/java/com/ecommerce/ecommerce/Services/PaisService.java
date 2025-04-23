package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Pais;
import com.ecommerce.ecommerce.Repositories.PaisRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaisService implements BaseService<Pais>{
    @Autowired
    private PaisRepositorio countryRepository;

    @Override
    public List<Pais> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Pais finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Pais save(Pais entity) throws Exception {
        return null;
    }

    @Override
    public Pais update(Long id, Pais newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
