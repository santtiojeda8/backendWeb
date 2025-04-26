package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Descuento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DescuentosRepository extends BaseRepository<Descuento, Long> {

    // Método simplificado usando la convención findById
    Optional<Descuento> findById(Long descuentoId);

    // Consulta para obtener descuentos por ID de producto
    @Query("SELECT d FROM Descuento d WHERE d.producto.id = :productoId")
    List<Descuento> findAllByProductoId(@Param("productoId") Long productoId);
}
