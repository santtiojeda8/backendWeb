package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends BaseRepository<Cliente, Long> {
    // Método para buscar cliente por ID de imagen (en este caso, el ID de imagen está en la entidad 'Imagen')
    Cliente findByImagenUser_Id(Long idImagen);  // Acceder al ID de la imagen relacionada
}
