package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.enums.Color;
import com.ecommerce.ecommerce.Entities.enums.Talle;
import com.ecommerce.ecommerce.Repositories.ProductoDetalleRepository;
import org.springframework.transaction.annotation.Transactional; // Importación correcta
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // Importar Optional

@Service
public class ProductoDetalleService extends BaseService<ProductoDetalle, Long> {
    private final ProductoDetalleRepository productoDetalleRepository;

    public ProductoDetalleService(ProductoDetalleRepository productoDetalleRepository) {
        super(productoDetalleRepository);
        this.productoDetalleRepository = productoDetalleRepository;
    }

    // --- Métodos del Servicio (que usan el Repositorio) ---

    // Este método ya debería filtrar por activo si tu findAllByProductoId
    // en el repositorio se encarga de eso, o si ProductoDetalle también tiene 'activo'.
    // Si ProductoDetalle extiende Base, y productoId es un campo de ProductoDetalle,
    // entonces la consulta JPA de findAllByProductoId() NO filtrará por `activo` por defecto.
    // NECESITARÁS UN MÉTODO EN EL REPOSITORIO COMO:
    // List<ProductoDetalle> findAllByProductoIdAndActivoTrue(Long productoId);
    @Transactional
    public List<ProductoDetalle> findAllByProductoId(Long productoId) throws Exception {
        try {
            // Asumiendo que ProductoDetalle extiende Base, y quieres solo los detalles activos
            return productoDetalleRepository.findAllByProductoIdAndActivoTrue(productoId); // <--- CAMBIO AQUÍ
        } catch (Exception e) {
            throw new Exception("Error al buscar ProductoDetalles por ID de Producto: " + e.getMessage());
        }
    }

    @Transactional
    public ProductoDetalle findByProductoIdAndTalleAndColor(Long productoId, Talle talle, Color color) throws Exception {
        try {
            // Este método en el repositorio también debería filtrar por activo
            Optional<ProductoDetalle> detalleOptional = productoDetalleRepository.findByProductoIdAndTalleAndColorAndActivoTrue(productoId, talle, color); // <--- CAMBIO AQUÍ
            if (detalleOptional.isEmpty()) {
                // Si no se encuentra o no está activo, lanza una excepción
                throw new Exception("ProductoDetalle no encontrado o inactivo para el ProductoId: " + productoId + ", Talle: " + talle + ", Color: " + color);
            }
            return detalleOptional.get();
        } catch (Exception e) {
            throw new Exception("Error al buscar ProductoDetalle por ProductoId, Talle y Color: " + e.getMessage());
        }
    }

    // Este método debería mostrar solo los detalles de productos activos
    @Transactional
    public List<ProductoDetalle> findAllByStockActualGreaterThan(Integer stockMinimo) throws Exception {
        try {
            // Añadir el filtro `AndActivoTrue`
            return productoDetalleRepository.findAllByStockActualGreaterThanAndActivoTrue(stockMinimo); // <--- CAMBIO AQUÍ
        } catch (Exception e) {
            throw new Exception("Error al buscar ProductoDetalles con stock mayor a: " + e.getMessage());
        }
    }

    // Este método de filtro principal también debe considerar el campo 'activo'
    @Transactional
    public List<ProductoDetalle> filtrarPorOpciones(Long productoId, Color color, Talle talle, Integer stockMin) throws Exception {
        try {
            // El método del repositorio debe incluir el filtro por activo
            return productoDetalleRepository.filtrarPorOpciones(productoId, color, talle, stockMin); // <--- VERIFICAR EN EL REPOSITORIO
        } catch (Exception e) {
            throw new Exception("Error al filtrar ProductoDetalles por opciones: " + e.getMessage());
        }
    }

    @Transactional
    public List<Talle> obtenerTallesDisponibles(Long productoId) throws Exception {
        try {
            // Este método debe obtener talles de detalles de productos que estén activos
            return productoDetalleRepository.obtenerTallesDisponibles(productoId); // <--- VERIFICAR EN EL REPOSITORIO
        } catch (Exception e) {
            throw new Exception("Error al obtener talles disponibles: " + e.getMessage());
        }
    }

    @Transactional
    public List<Color> obtenerColoresDisponibles(Long productoId) throws Exception {
        try {
            // Este método debe obtener colores de detalles de productos que estén activos
            return productoDetalleRepository.obtenerColoresDisponibles(productoId); // <--- VERIFICAR EN EL REPOSITORIO
        } catch (Exception e) {
            throw new Exception("Error al obtener colores disponibles: " + e.getMessage());
        }
    }

    @Transactional
    public void descontarStock(Long productoDetalleId, int cantidad) throws Exception {
        try {
            // Buscar por ID y activo=true para asegurarse de que solo se descuente stock de productos activos
            ProductoDetalle detalle = productoDetalleRepository.findByIdAndActivoTrue(productoDetalleId) // <--- CAMBIO AQUÍ
                    .orElseThrow(() -> new RuntimeException("ProductoDetalle no encontrado o inactivo con ID: " + productoDetalleId));
            if (detalle.getStockActual() < cantidad) {
                throw new IllegalArgumentException("Stock insuficiente para el ProductoDetalle ID: " + productoDetalleId);
            }
            detalle.setStockActual(detalle.getStockActual() - cantidad);
            productoDetalleRepository.save(detalle);
        } catch (Exception e) {
            throw new Exception("Error al descontar stock: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public boolean estaDisponible(Long productoId, Talle talle, Color color) throws Exception {
        try {
            // Usa el método que filtra por activo
            Optional<ProductoDetalle> detalleOptional = productoDetalleRepository.findByProductoIdAndTalleAndColorAndActivoTrue(productoId, talle, color); // <--- CAMBIO AQUÍ
            return detalleOptional.isPresent() && detalleOptional.get().getStockActual() > 0;
        } catch (Exception e) {
            throw new Exception("Error al verificar disponibilidad: " + e.getMessage());
        }
    }
}