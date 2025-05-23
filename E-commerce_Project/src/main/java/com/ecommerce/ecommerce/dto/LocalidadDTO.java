// src/main/java/com/ecommerce/ecommerce/dto/LocalidadDTO.java
package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocalidadDTO {
    private Long id; // Fundamental
    private String nombre;
    private ProvinciaDTO provincia; // Anidado para mostrar provincia
}