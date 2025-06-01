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
public class ProductoDetalleDTO {
    private Long id;
    private BigDecimal precioCompra;
    private Integer stockActual;
    private Integer stockMaximo;
    private String color;
    private String talle;
    private Integer cantidad; // <--- Añadido el campo 'cantidad'
    private boolean activo;   // <--- Cambiado a 'activo' para consistencia con el backend
    private ProductoDTO producto; // <--- Añadida la referencia al producto padre para setProducto
}