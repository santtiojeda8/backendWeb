package com.ecommerce.ecommerce.Repositories;


import com.ecommerce.ecommerce.Entities.Localidad;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalidadRepository extends BaseRepository<Localidad,Long>{
    List<Localidad> findAllByProvinciaId(Long idProvincia);
}
