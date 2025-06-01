package com.ecommerce.ecommerce.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDetalleRequestDTO {
    private Long id; // Null para nuevos detalles, presente para detalles existentes
    private BigDecimal precioCompra;
    private Integer stockActual;
    private Integer stockMaximo;
    private String color; // Mantener como String para facilitar el envío desde el frontend
    private String talle; // Mantener como String para facilitar el envío desde el frontend
    private boolean activo;

}