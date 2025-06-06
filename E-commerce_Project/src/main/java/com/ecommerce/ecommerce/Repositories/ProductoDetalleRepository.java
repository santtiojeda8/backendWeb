package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.Color; // Importar la entidad Color
import com.ecommerce.ecommerce.Entities.Talle; // Importar la entidad Talle
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoDetalleRepository extends BaseRepository<ProductoDetalle, Long> {

    // --- Métodos de Spring Data JPA con nombre de método ---
    // Ahora usando las entidades Color y Talle
    List<ProductoDetalle> findAllByProductoIdAndActivoTrue(Long productoId);

    // Por talle y color de un producto, solo si está activo. Retorna Optional por si no se encuentra.
    Optional<ProductoDetalle> findByProductoIdAndTalleAndColorAndActivoTrue(Long productoId, Talle talle, Color color);

    // Stock mayor a cierto mínimo, solo si están activos
    List<ProductoDetalle> findAllByStockActualGreaterThanAndActivoTrue(Integer stockMinimo);

    // Por color específico, solo si están activos
    List<ProductoDetalle> findAllByColorAndActivoTrue(Color color);

    // Por talle específico, solo si están activos
    List<ProductoDetalle> findAllByTalleAndActivoTrue(Talle talle);

    // Por producto y color, solo si están activos
    List<ProductoDetalle> findAllByProductoIdAndColorAndActivoTrue(Long productoId, Color color);

    // Por producto y talle, solo si están activos
    List<ProductoDetalle> findAllByProductoIdAndTalleAndActivoTrue(Long productoId, Talle talle);

    // Stock entre un rango, solo si están activos
    List<ProductoDetalle> findAllByStockActualBetweenAndActivoTrue(Integer stockMin, Integer stockMax);

    // --- Métodos con @Query ---
    // Las consultas se ajustan para referenciar las propiedades de las entidades Color y Talle
    @Query("SELECT pd FROM ProductoDetalle pd " +
            "WHERE pd.activo = true " +
            "AND pd.producto.id = :productoId " +
            "AND (:colorNombre IS NULL OR pd.color.nombreColor = :colorNombre) " + // Ahora compara por nombreColor
            "AND (:talleNombre IS NULL OR pd.talle.nombreTalle = :talleNombre) " + // Ahora compara por nombreTalle
            "AND pd.stockActual >= :stockMin")
    List<ProductoDetalle> filtrarPorOpciones(
            @Param("productoId") Long productoId,
            @Param("colorNombre") String colorNombre, // Parámetro como String
            @Param("talleNombre") String talleNombre, // Parámetro como String
            @Param("stockMin") Integer stockMin
    );

    @Query("SELECT DISTINCT pd.talle.nombreTalle FROM ProductoDetalle pd WHERE pd.activo = true AND pd.producto.id = :productoId") // Selecciona el nombre del talle
    List<String> obtenerNombresTallesDisponibles(@Param("productoId") Long productoId); // Retorna List<String>

    @Query("SELECT DISTINCT pd.color.nombreColor FROM ProductoDetalle pd WHERE pd.activo = true AND pd.producto.id = :productoId") // Selecciona el nombre del color
    List<String> obtenerNombresColoresDisponibles(@Param("productoId") Long productoId); // Retorna List<String>
}
