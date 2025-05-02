package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Descuento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DescuentosRepository extends BaseRepository<Descuento, Long> {


}