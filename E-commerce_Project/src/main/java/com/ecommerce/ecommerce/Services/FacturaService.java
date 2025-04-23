package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Factura;
import com.ecommerce.ecommerce.Repositories.FacturaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaService implements BaseService<Factura>{
    @Autowired
    private FacturaRepositorio billRepository;

    @Override
    public List<Factura> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Factura finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Factura save(Factura entity) throws Exception {
        return null;
    }

    @Override
    public Factura update(Long id, Factura newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
