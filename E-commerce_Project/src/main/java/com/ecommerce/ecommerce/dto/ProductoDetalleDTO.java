package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.ProductoDetalle;
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
    private boolean activo;

    // Campos para recibir los IDs de Color y Talle (para entrada)
    private Long colorId; // <-- Agregado para recibir el ID del color
    private Long talleId; // <-- Agregado para recibir el ID del talle
    private Long productoId; // <-- Agregado para recibir el ID del producto asociado

    // Campos para enviar el Color y Talle como DTOs anidados (para salida)
    private ColorDTO color;
    private TalleDTO talle;

    // Campos adicionales para información del producto asociado (para salida)
    private String productoDenominacion;

    private Integer cantidad; // Este campo podría ser stockActual o un campo temporal para operaciones.

    public ProductoDetalleDTO(ProductoDetalle entity) {
        this.id = entity.getId();
        this.precioCompra = entity.getPrecioCompra();
        this.stockActual = entity.getStockActual();
        this.stockMaximo = entity.getStockMaximo();
        this.activo = entity.isActivo();

        // Para el DTO de salida, si la entidad tiene Color/Talle, creamos sus DTOs
        if (entity.getColor() != null) {
            this.color = new ColorDTO(entity.getColor());
            this.colorId = entity.getColor().getId(); // También llenamos el ID para consistencia
        }
        if (entity.getTalle() != null) {
            this.talle = new TalleDTO(entity.getTalle());
            this.talleId = entity.getTalle().getId(); // También llenamos el ID para consistencia
        }

        if (entity.getProducto() != null) {
            this.productoId = entity.getProducto().getId();
            this.productoDenominacion = entity.getProducto().getDenominacion();
        }
        this.cantidad = entity.getStockActual(); // Asumiendo que cantidad es stockActual
    }
}