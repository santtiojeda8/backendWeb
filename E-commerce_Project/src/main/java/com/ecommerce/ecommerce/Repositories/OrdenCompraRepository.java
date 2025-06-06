package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends BaseRepository<OrdenCompra, Long> {

    // 1. Para el método findAllDTO() del servicio, si BaseRepository no filtra por activo.
    // Aunque BaseRepository ya tiene findAllByActivoTrue(), lo mantengo para claridad.
    List<OrdenCompra> findAllByActivoTrue();

    // 2. Para el método findByIdDTO() del servicio, si BaseRepository no filtra por activo.
    // Aunque BaseRepository ya tiene findByIdAndActivoTrue(), lo mantengo para claridad.
    Optional<OrdenCompra> findByIdAndActivoTrue(Long id);

    // 3. ¡CORRECCIÓN CLAVE! Este método es el que causaba el error en el controlador.
    List<OrdenCompra> findByUsuarioIdAndActivoTrue(Long usuarioId);

    // 4. Corrección: El método para buscar por fecha.
    // El nombre que causaba problemas era 'findAllByFechaCompra'.
    // Tu servicio espera 'findByFechaCompraBetweenAndActivoTrue'.
    List<OrdenCompra> findByFechaCompraBetweenAndActivoTrue(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // Considera si necesitas un método para buscar por fecha posterior (After) o anterior (Before)
    // List<OrdenCompra> findByFechaCompraAfterAndActivoTrue(LocalDateTime fechaCompra);
    // List<OrdenCompra> findByFechaCompraBeforeAndActivoTrue(LocalDateTime fechaCompra);
}