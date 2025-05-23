// src/main/java/com/ecommerce/ecommerce/dto/DomicilioDTO.java (MODIFICAR)
package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomicilioDTO {
    private Long id;
    private String calle;
    private int numero;
    private String piso;
    private String departamento;
    private int cp;
    private LocalidadDTO localidad; // <--- ¡CAMBIADO! Ahora es un objeto LocalidadDTO

}