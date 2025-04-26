package com.ecommerce.ecommerce.Repositories;


import com.ecommerce.ecommerce.Entities.OrdenCompra;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdenCompraRepository extends BaseRepository<OrdenCompra, Long> {
    //todas las ordenes con una fecha específica
    List<OrdenCompra> findAllByFechaCompra(LocalDateTime fecha);

    // todas las ordenes que usaron un descuento específico
    List<OrdenCompra> findAllByDescuentoId(Long idDescuento);
}
