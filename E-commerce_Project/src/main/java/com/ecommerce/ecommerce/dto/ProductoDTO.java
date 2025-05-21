package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    private Long id;
    private String denominacion;
    private Double precioOriginal; // Precio de venta de la entidad Producto
    private Double precioFinal;    // Precio calculado con descuentos
    private boolean tienePromocion;
    private Sexo sexo; // Tipo Enum o String según tu necesidad en el DTO
    private List<CategoriaDTO> categorias; // Lista de DTOs de categoría
    private List<ImagenDTO> imagenes;     // Lista de DTOs de imagen
    private List<ProductoDetalleDTO> productos_detalles; // Lista de DTOs de detalle de producto
    // No se exponen directamente los descuentos en el ProductoDTO para el frontend,
    // el precioFinal ya refleja el descuento.
}