package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Detalle;
import com.ecommerce.ecommerce.Repositories.DetalleRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleService implements BaseService<Detalle>{

    @Autowired
    private DetalleRepositorio detailsRepository;

    @Override
    public List<Detalle> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Detalle finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Detalle save(Detalle entity) throws Exception {
        return null;
    }

    @Override
    public Detalle update(Long id, Detalle newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
