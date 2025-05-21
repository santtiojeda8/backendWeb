package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDetalleDTO {
    private Long id;
    private Double precioCompra;
    private Integer stockActual;
    private Integer cantidad; // Revisa si este campo es relevante en el DTO para el frontend. Podría ser 'cantidadVendida' si se usa en ordenes.
    private Integer stockMaximo;
    private String color; // String para el DTO
    private String talle; // String para el DTO
    // No incluyas el Producto aquí para evitar recursión infinita en el mapeo
}