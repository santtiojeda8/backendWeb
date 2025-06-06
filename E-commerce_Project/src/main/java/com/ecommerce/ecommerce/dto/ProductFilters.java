// src/main/java/com/ecommerce/ecommerce/dto/ProductFilters.java
package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo; // Assuming Sexo enum is here
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // IMPORTANTE: Asegúrate de importar BigDecimal
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilters {
    private String denominacion;
    private List<String> categorias;
    private Sexo sexo;
    private Boolean tienePromocion;
    private BigDecimal minPrice; // CAMBIO CLAVE: Asegúrate de que sea BigDecimal
    private BigDecimal maxPrice; // CAMBIO CLAVE: Asegúrate de que sea BigDecimal
    private List<String> colores;
    private List<String> talles;
    private Integer stockMinimo;
    private String orderBy;
    private String orderDirection;
}