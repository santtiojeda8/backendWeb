package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // <--- ¡ASEGÚRATE DE ESTA IMPORTACIÓN!

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraDetalleDTO {
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario; // <--- ¡DEBE SER BIGDECIMAL!
    private BigDecimal subtotal;       // <--- ¡DEBE SER BIGDECIMAL!
    private Long ordenCompraId;
    private Long productoDetalleId;

    private ProductoDetalleNestedDTO productoDetalle;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoDetalleNestedDTO {
        private Long id;
        private BigDecimal precioCompra; // <--- ¡DEBE SER BIGDECIMAL!
        private Integer stockActual;
        private Integer stockMaximo;
        private String color;
        private String talle;
        private String productoDenominacion;
    }
}