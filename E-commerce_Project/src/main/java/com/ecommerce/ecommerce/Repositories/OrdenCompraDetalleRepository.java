package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Importar Optional

@Repository
public interface OrdenCompraDetalleRepository extends BaseRepository<OrdenCompraDetalle, Long> {

    // Método para obtener todos los detalles de orden de compra que estén activos
    List<OrdenCompraDetalle> findAllByActivoTrue();

    // Método para buscar un detalle de orden de compra por su ID y que esté activo
    Optional<OrdenCompraDetalle> findByIdAndActivoTrue(Long id);

    // Buscar todos los detalles de una orden de compra específica y que estén activos
    List<OrdenCompraDetalle> findByOrdenCompraIdAndActivoTrue(Long ordenCompraId);

    // Buscar detalles por un producto detalle específico y que estén activos
    List<OrdenCompraDetalle> findByProductoDetalleIdAndActivoTrue(Long productoDetalleId);

    // Estos métodos ya los tenías, pero asegúrate de que no entren en conflicto con los nuevos si no los usas:
    // List<OrdenCompraDetalle> findAllByOrdenCompraId(Long ordenCompraId);
    // List<OrdenCompraDetalle> findAllByProductoDetalleId(Long productoDetalleId);
}