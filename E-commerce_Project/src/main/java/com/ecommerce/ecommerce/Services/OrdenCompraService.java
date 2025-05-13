package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Repositories.OrdenCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set; // Importar Set

@Service
public class OrdenCompraService extends BaseService<OrdenCompra, Long> {

    private final OrdenCompraRepository ordenCompraRepository;

    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository) {
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;
    }

    @Override
    @Transactional // Asegura que la operación sea atómica
    public OrdenCompra crear(OrdenCompra ordenCompra) throws Exception {
        try {
            if (ordenCompra.getFechaCompra() == null) {
                ordenCompra.setFechaCompra(LocalDateTime.now());
            }

            // Validar que haya detalles antes de procesar
            if (ordenCompra.getDetalles() == null || ordenCompra.getDetalles().isEmpty()) {
                throw new Exception("La orden debe tener al menos un producto.");
            }

            // --- Lógica Modificada ---
            // Iteramos sobre una copia del conjunto de detalles para evitar ConcurrentModificationException
            // si la colección original es modificada durante la iteración (aunque addDetalle modifica la original,
            // es más seguro iterar sobre una copia si la colección original viene de fuera).
            // Opcional: Si estás seguro de que el Set recibido no será modificado externamente,
            // puedes iterar directamente sobre ordenCompra.getDetalles().
            Set<OrdenCompraDetalle> detallesOriginales = ordenCompra.getDetalles();
            ordenCompra.setDetalles(new HashSet<>()); // Limpiamos el set original para usar addDetalle

            for (OrdenCompraDetalle detalle : detallesOriginales) {
                // Usamos el método addDetalle de la entidad OrdenCompra
                // Este método establece la relación bidireccional (detalle.setOrdenCompra(ordenCompra))
                // Y llama a recalcularTotal() en la orden.
                // El subtotal del detalle se calculará automáticamente con @PrePersist
                ordenCompra.addDetalle(detalle);
            }

            // El total de la orden ya fue calculado por addDetalle y se recalculará
            // una última vez por el @PrePersist/@PreUpdate en la entidad OrdenCompra
            // justo antes de guardar. No necesitamos calcularlo manualmente aquí.
            // ordenCompra.setTotal(totalCalculado); <-- Eliminar esta línea

            // La asignación de referencia de orden a cada detalle ya la hace addDetalle
            // for (OrdenCompraDetalle detalle : ordenCompra.getDetalles()) { <-- Eliminar este bucle
            //     detalle.setOrdenCompra(ordenCompra);
            // }

            // Guardar la orden (esto persistirá la orden y en cascada sus detalles)
            return super.crear(ordenCompra);

        } catch (Exception e) {
            // Es buena práctica loggear la excepción original
            // logger.error("Error al crear orden de compra", e);
            throw new Exception("Error al crear la orden de compra: " + e.getMessage());
        }
    }

    @Transactional // Añadir @Transactional si no está en BaseService
    public List<OrdenCompra> obtenerPorFecha(LocalDateTime fecha) throws Exception {
        try {
            return ordenCompraRepository.findAllByFechaCompra(fecha);
        } catch (Exception e) {
            // logger.error("Error al obtener ordenes por fecha", e);
            throw new Exception("Error al obtener ordenes por fecha: " + e.getMessage());
        }
    }

    // Si tienes otros métodos para actualizar la orden (ej. añadir/quitar detalles después de la creación)
    // también deberías usar addDetalle/removeDetalle en esos métodos.
}
