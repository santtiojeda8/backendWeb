package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Imagen;
import com.ecommerce.ecommerce.Repositories.ImagenRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService implements BaseService<Imagen>{

    @Autowired
    private ImagenRepositorio imageRepository;

    @Override
    public List<Imagen> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Imagen finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Imagen save(Imagen entity) throws Exception {
        return null;
    }

    @Override
    public Imagen update(Long id, Imagen newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
