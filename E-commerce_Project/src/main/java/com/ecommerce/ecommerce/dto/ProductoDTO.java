package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Importar BigDecimal

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String denominacion;
    private BigDecimal precioOriginal; // Asegúrate de que sea BigDecimal
    private BigDecimal precioFinal;    // Asegúrate de que sea BigDecimal
    private boolean tienePromocion;
    private Sexo sexo;
    private boolean activo;

    private List<CategoriaDTO> categorias;
    private List<ImagenDTO> imagenes;
    private List<ProductoDetalleDTO> productos_detalles;

    private DescuentoDTO descuento; // <--- ¡Asegúrate de que este campo exista!
}