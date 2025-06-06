package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Import BigDecimal for precioOriginal
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {
    private String denominacion;
    private BigDecimal precioOriginal; // CHANGED: From Double to BigDecimal
    private boolean tienePromocion;
    private Sexo sexo;
    private boolean activo;

    private Set<Long> categoriaIds;
    private List<ImagenDTO> imagenes;
    private List<ProductoDetalleRequestDTO> productos_detalles;

    // CHANGED: Now expects a nested DescuentoDTO
    private DescuentoDTO descuento;
}