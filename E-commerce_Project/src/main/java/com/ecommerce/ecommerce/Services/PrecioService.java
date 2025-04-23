package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Precio;
import com.ecommerce.ecommerce.Repositories.PrecioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrecioService implements BaseService<Precio>{

    @Autowired
    private PrecioRepositorio pricesRepository;

    @Override
    public List<Precio> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Precio finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Precio save(Precio entity) throws Exception {
        return null;
    }

    @Override
    public Precio update(Long id, Precio newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
