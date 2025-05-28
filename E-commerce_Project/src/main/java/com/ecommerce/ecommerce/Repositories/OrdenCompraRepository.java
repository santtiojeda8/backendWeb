package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Necesario para findByIdAndActivoTrue

@Repository
// Asumo que BaseRepository<OrdenCompra, Long> ya te da los métodos CRUD básicos.
// Si no te da findAllByActivoTrue o findByIdAndActivoTrue, deberías añadirlos aquí.
public interface OrdenCompraRepository extends BaseRepository<OrdenCompra, Long> {

    // --- Métodos específicos para OrdenCompra requeridos por el servicio ---

    // 1. Para el método findAllDTO() del servicio, si BaseRepository no filtra por activo.
    // Si tu BaseRepository ya tiene un findAll() que filtra por 'activo = true',
    // o si el 'super.listar()' en el servicio ya lo hace, entonces esta línea no es estrictamente necesaria aquí.
    // Pero es una buena práctica tener un método explícito si no estás seguro.
    List<OrdenCompra> findAllByActivoTrue();

    // 2. Para el método findByIdDTO() del servicio, si BaseRepository no filtra por activo.
    // Similar al anterior, si tu BaseRepository.buscarPorId() no filtra por 'activo = true', añádelo.
    Optional<OrdenCompra> findByIdAndActivoTrue(Long id);

    // 3. ¡EL MÉTODO QUE FALTABA Y CAUSA EL ERROR DE "Cannot resolve method 'obtenerPorUsuarioDTO'"!
    List<OrdenCompra> findByUsuarioIdAndActivoTrue(Long usuarioId);

    // 4. El método que ya tenías para buscar por fecha específica.
    // Asegúrate de que el nombre del campo en tu entidad OrdenCompra es 'fechaCompra'.
    List<OrdenCompra> findAllByFechaCompra(LocalDateTime fechaCompra);

    // Considera si necesitas un método para buscar por fecha posterior (After) o anterior (Before)
    // List<OrdenCompra> findByFechaCompraAfterAndActivoTrue(LocalDateTime fechaCompra);
    // List<OrdenCompra> findByFechaCompraBeforeAndActivoTrue(LocalDateTime fechaCompra);
}