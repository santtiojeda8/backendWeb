package com.ecommerce.ecommerce.Repositories; // Asegúrate de que el paquete sea correcto

import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Repositories.BaseRepository; // Assuming this path is correct for BaseRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Importar Param
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends BaseRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    // Método para buscar todos los productos activos, cargando el descuento
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.descuento WHERE p.activo = true")
    List<Producto> findAllByActivoTrueWithDescuento();

    // Método para buscar un producto activo por ID, cargando el descuento
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.descuento WHERE p.id = :id AND p.activo = true")
    Optional<Producto> findByIdAndActivoTrueWithDescuento(@Param("id") Long id);

    // Método para buscar por denominación, cargando el descuento
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.descuento WHERE LOWER(p.denominacion) LIKE LOWER(CONCAT('%', :denominacion, '%')) AND p.activo = true")
    List<Producto> findByDenominacionContainingAndActivoTrueWithDescuento(@Param("denominacion") String denominacion);

    // NUEVO MÉTODO: Buscar productos con promoción activa y que estén activos
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.descuento d " +
            "WHERE p.tienePromocion = true " +
            "AND p.activo = true " +
            "AND d.activo = true " + // Ensure the discount itself is active
            "AND CURRENT_TIMESTAMP BETWEEN d.fechaDesde AND d.fechaHasta " + // Check date validity
            "AND CURRENT_TIME BETWEEN d.horaDesde AND d.horaHasta")         // Check time validity
    List<Producto> findByTienePromocionTrueAndActivoTrueWithDescuento();
}