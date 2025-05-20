package com.ecommerce.ecommerce.Repositories;


import com.ecommerce.ecommerce.Entities.Direccion;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionRepository extends BaseRepository<Direccion,Long> {
    List<Direccion> findAllByLocalidadId(Long idLocalidad);

    default List<Direccion> findAllByClientesAndId(Long idCliente) {
        return null;
    }

}