package com.ecommerce.ecommerce.Repositories;


import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.enums.Color;
import com.ecommerce.ecommerce.Entities.enums.Talle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoDetalleRepository extends BaseRepository<ProductoDetalle, Long> {

    // Todos los detalles de un producto
    List<ProductoDetalle> findAllByProductoId(Long productoId);

    // Por talle y color de un producto
    ProductoDetalle findByProductoIdAndTalleAndColor(Long productoId, Talle talle, Color color);

    // Stock mayor a cierto mínimo
    List<ProductoDetalle> findAllByStockActualGreaterThan(Integer stockMinimo);

    // Por color específico
    List<ProductoDetalle> findAllByColor(Color color);

    // Por talle específico
    List<ProductoDetalle> findAllByTalle(Talle talle);

    // Por producto y color
    List<ProductoDetalle> findAllByProductoIdAndColor(Long productoId, Color color);

    // Por producto y talle
    List<ProductoDetalle> findAllByProductoIdAndTalle(Long productoId, Talle talle);

    // Stock entre un rango
    List<ProductoDetalle> findAllByStockActualBetween(Integer stockMin, Integer stockMax);

    @Query("SELECT pd FROM ProductoDetalle pd " +
            "WHERE pd.producto.id = :productoId " +
            "AND (:color IS NULL OR pd.color = :color) " +
            "AND (:talle IS NULL OR pd.talle = :talle) " +
            "AND pd.stockActual >= :stockMin")
    List<ProductoDetalle> filtrarPorOpciones(
            @Param("productoId") Long productoId,
            @Param("color") Color color,
            @Param("talle") Talle talle,
            @Param("stockMin") Integer stockMin
    );
    @Query("SELECT DISTINCT pd.talle FROM ProductoDetalle pd WHERE pd.producto.id = :productoId")
    List<Talle> obtenerTallesDisponibles(@Param("productoId") Long productoId);
    @Query("SELECT DISTINCT pd.color FROM ProductoDetalle pd WHERE pd.producto.id = :productoId")
    List<Color> obtenerColoresDisponibles(@Param("productoId") Long productoId);

}
