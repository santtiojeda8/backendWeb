package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Repositories.DireccionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DireccionService implements BaseService<Direccion>{

    @Autowired
    private DireccionRepositorio adressRepository;

    @Override
    public List<Direccion> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Direccion finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Direccion save(Direccion entity) throws Exception {
        return null;
    }

    @Override
    public Direccion update(Long id, Direccion newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
