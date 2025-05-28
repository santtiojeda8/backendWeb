// com/ecommerce/ecommerce/dto/OrdenCompraDetalleDTO.java (MODIFICADO Y CORREGIDO)
package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraDetalleDTO {
    private Long id;
    private Integer cantidad;
    private Double subtotal;
    private Long ordenCompraId; // Agregado para la relación con OrdenCompra
    private Long productoDetalleId; // Agregado para la relación con ProductoDetalle

    // Asegúrate de que esta clase anidada sea 'public static'
    private ProductoDetalleNestedDTO productoDetalle; // Campo para el objeto anidado

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoDetalleNestedDTO { // <--- CLAVE: public static
        private Long id;
        private Double precioCompra;
        private Integer stockActual;
        private Integer stockMaximo;
        private String color;
        private String talle;
        private String productoDenominacion; // <--- ¡AÑADIDO ESTE CAMPO AQUÍ!
        // Si necesitas el ID del Producto padre, lo puedes añadir aquí:
        // private Long productoPadreId;
    }
}