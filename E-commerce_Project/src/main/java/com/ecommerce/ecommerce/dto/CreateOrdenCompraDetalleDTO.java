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
public class CreateOrdenCompraDetalleDTO {
    private Long productoDetalleId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}