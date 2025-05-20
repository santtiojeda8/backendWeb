package com.ecommerce.ecommerce.Repositories;


import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraDetalleRepository extends BaseRepository<OrdenCompraDetalle, Long> {

    // Buscar todos los detalles de una orden
    List<OrdenCompraDetalle> findAllByOrdenCompraId(Long ordenCompraId);

    // Buscar por producto específico en los detalles
    List<OrdenCompraDetalle> findAllByProductoDetalleId(Long productoDetalleId);


}
