package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Repositories.LocalidadRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalidadService implements BaseService<LocalidadService>{

    @Autowired
    private LocalidadRepositorio localityRepository;


    @Override
    public List<LocalidadService> findAll() throws Exception {
        return List.of();
    }

    @Override
    public LocalidadService finById(Long id) throws Exception {
        return null;
    }

    @Override
    public LocalidadService save(LocalidadService entity) throws Exception {
        return null;
    }

    @Override
    public LocalidadService update(Long id, LocalidadService newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
