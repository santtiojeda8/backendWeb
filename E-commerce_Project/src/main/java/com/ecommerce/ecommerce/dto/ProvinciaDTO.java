// src/main/java/com/ecommerce/ecommerce/dto/ProvinciaDTO.java
package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProvinciaDTO {
    private Long id; // Fundamental
    private String nombre;
}