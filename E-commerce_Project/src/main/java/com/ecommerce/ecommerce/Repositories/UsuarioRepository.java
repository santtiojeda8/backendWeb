package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
}
