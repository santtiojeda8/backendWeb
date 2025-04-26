package com.ecommerce.ecommerce.Services;



import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService extends BaseService<Usuario, Long> {
    public UsuarioService(UsuarioRepository usuarioRepository){
        super(usuarioRepository);
    }
}
