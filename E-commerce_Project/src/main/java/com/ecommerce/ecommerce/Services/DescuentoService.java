package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Descuento;
import com.ecommerce.ecommerce.Repositories.DescuentoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DescuentoService implements BaseService<Descuento>{

    @Autowired
    private DescuentoRepositorio discountRepository;

    @Override
    public List<Descuento> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Descuento finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Descuento save(Descuento entity) throws Exception {
        return null;
    }

    @Override
    public Descuento update(Long id, Descuento newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
