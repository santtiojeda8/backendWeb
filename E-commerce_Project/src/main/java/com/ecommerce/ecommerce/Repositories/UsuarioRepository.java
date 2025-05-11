package com.ecommerce.ecommerce.Repositories;

import java.util.Optional;
import com.ecommerce.ecommerce.Entities.Usuario;
import org.springframework.stereotype.Repository;

@Repository
// Asegúrate de que extienda de BaseRepository<Usuario, Long> como ya lo tienes
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {


    Optional<Usuario> findByUserName(String userName);


}