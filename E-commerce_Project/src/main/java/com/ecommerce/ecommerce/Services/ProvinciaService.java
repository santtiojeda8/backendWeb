package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Provincia;
import com.ecommerce.ecommerce.Repositories.ProvinciaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinciaService implements BaseService<Provincia>{

    @Autowired
    private ProvinciaRepositorio provinceRepository;

    @Override
    public List<Provincia> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Provincia finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Provincia save(Provincia entity) throws Exception {
        return null;
    }

    @Override
    public Provincia update(Long id, Provincia newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
