package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Repositories.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements BaseService<Usuario>{
    @Autowired
    private UsuarioRepositorio userRepository;

    @Override
    public List<Usuario> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Usuario finById(Long id) throws Exception {
        return null;
    }

    @Override
    public Usuario save(Usuario entity) throws Exception {
        return null;
    }

    @Override
    public Usuario update(Long id, Usuario newEntity) throws Exception {
        return null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return false;
    }
}
