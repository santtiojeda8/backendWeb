package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.Color;
import com.ecommerce.ecommerce.Entities.Talle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoDetalleRepository extends BaseRepository<ProductoDetalle, Long> {

    // --- Métodos para el CLIENTE (solo activos) ---
    List<ProductoDetalle> findAllByProductoIdAndActivoTrue(Long productoId);
    Optional<ProductoDetalle> findByProductoIdAndTalleAndColorAndActivoTrue(Long productoId, Talle talle, Color color);
    List<ProductoDetalle> findAllByStockActualGreaterThanAndActivoTrue(Integer stockMinimo);
    List<ProductoDetalle> findAllByColorAndActivoTrue(Color color);
    List<ProductoDetalle> findAllByTalleAndActivoTrue(Talle talle);
    List<ProductoDetalle> findAllByProductoIdAndColorAndActivoTrue(Long productoId, Color color);
    List<ProductoDetalle> findAllByProductoIdAndTalleAndActivoTrue(Long productoId, Talle talle);
    List<ProductoDetalle> findAllByStockActualBetweenAndActivoTrue(Integer stockMin, Integer stockMax);

    // --- NUEVO MÉTODO para la ADMINISTRACIÓN (trae activos e inactivos) ---
    List<ProductoDetalle> findAllByProductoId(Long productoId); // No filtra por 'activo'

    // --- Métodos con @Query (Asegurarse de su uso: ¿cliente o admin?) ---
    // Si estos son para el cliente, entonces mantienen `pd.activo = true`
    @Query("SELECT pd FROM ProductoDetalle pd " +
            "WHERE pd.activo = true " + // Mantener si es para cliente
            "AND pd.producto.id = :productoId " +
            "AND (:colorNombre IS NULL OR pd.color.nombreColor = :colorNombre) " +
            "AND (:talleNombre IS NULL OR pd.talle.nombreTalle = :talleNombre) " +
            "AND pd.stockActual >= :stockMin")
    List<ProductoDetalle> filtrarPorOpciones(
            @Param("productoId") Long productoId,
            @Param("colorNombre") String colorNombre,
            @Param("talleNombre") String talleNombre,
            @Param("stockMin") Integer stockMin
    );

    @Query("SELECT DISTINCT pd.talle.nombreTalle FROM ProductoDetalle pd WHERE pd.activo = true AND pd.producto.id = :productoId")
    List<String> obtenerNombresTallesDisponibles(@Param("productoId") Long productoId);

    @Query("SELECT DISTINCT pd.color.nombreColor FROM ProductoDetalle pd WHERE pd.activo = true AND pd.producto.id = :productoId")
    List<String> obtenerNombresColoresDisponibles(@Param("productoId") Long productoId);

    // Si tu servicio ProductoDetalleService tiene un findByIdAndActivoTrue (como lo vimos antes)
    // entonces este método también es necesario en el repo base o aquí.
    // BaseRepository ya lo tiene, así que no necesitas redeclararlo aquí.
}