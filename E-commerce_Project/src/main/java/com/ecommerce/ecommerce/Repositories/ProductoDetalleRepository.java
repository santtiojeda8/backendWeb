package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.enums.Color;
import com.ecommerce.ecommerce.Entities.enums.Talle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // ¡Importante: Añadir esta importación para Optional!

@Repository
// Asegúrate de que ProductoDetalle extiende Base para que 'activo' esté disponible
public interface ProductoDetalleRepository extends BaseRepository<ProductoDetalle, Long> {

    // --- Métodos de Spring Data JPA con nombre de método ---
    // Añadimos 'AndActivoTrue' para que estos métodos generen la cláusula WHERE `activo = true`

    // Todos los detalles de un producto, solo si están activos
    List<ProductoDetalle> findAllByProductoIdAndActivoTrue(Long productoId); // <--- CAMBIADO

    // Por talle y color de un producto, solo si está activo. Retorna Optional por si no se encuentra.
    Optional<ProductoDetalle> findByProductoIdAndTalleAndColorAndActivoTrue(Long productoId, Talle talle, Color color); // <--- CAMBIADO

    // Stock mayor a cierto mínimo, solo si están activos
    List<ProductoDetalle> findAllByStockActualGreaterThanAndActivoTrue(Integer stockMinimo); // <--- CAMBIADO

    // Por color específico, solo si están activos
    List<ProductoDetalle> findAllByColorAndActivoTrue(Color color); // <--- CAMBIADO

    // Por talle específico, solo si están activos
    List<ProductoDetalle> findAllByTalleAndActivoTrue(Talle talle); // <--- CAMBIADO

    // Por producto y color, solo si están activos
    List<ProductoDetalle> findAllByProductoIdAndColorAndActivoTrue(Long productoId, Color color); // <--- CAMBIADO

    // Por producto y talle, solo si están activos
    List<ProductoDetalle> findAllByProductoIdAndTalleAndActivoTrue(Long productoId, Talle talle); // <--- CAMBIADO

    // Stock entre un rango, solo si están activos
    List<ProductoDetalle> findAllByStockActualBetweenAndActivoTrue(Integer stockMin, Integer stockMax); // <--- CAMBIADO

    // --- Métodos con @Query ---
    // Necesitamos añadir `AND pd.activo = true` en la cláusula WHERE de cada @Query

    @Query("SELECT pd FROM ProductoDetalle pd " +
            "WHERE pd.activo = true " + // <--- AÑADE ESTO
            "AND pd.producto.id = :productoId " +
            "AND (:color IS NULL OR pd.color = :color) " +
            "AND (:talle IS NULL OR pd.talle = :talle) " +
            "AND pd.stockActual >= :stockMin")
    List<ProductoDetalle> filtrarPorOpciones(
            @Param("productoId") Long productoId,
            @Param("color") Color color,
            @Param("talle") Talle talle,
            @Param("stockMin") Integer stockMin
    );

    @Query("SELECT DISTINCT pd.talle FROM ProductoDetalle pd WHERE pd.activo = true AND pd.producto.id = :productoId") // <--- AÑADE ESTO
    List<Talle> obtenerTallesDisponibles(@Param("productoId") Long productoId);

    @Query("SELECT DISTINCT pd.color FROM ProductoDetalle pd WHERE pd.activo = true AND pd.producto.id = :productoId") // <--- AÑADE ESTO
    List<Color> obtenerColoresDisponibles(@Param("productoId") Long productoId);

}