package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DireccionDTO {
    private Long id;
    private String calle;
    private int numero;
    private String piso; // <-- ¡NUEVO CAMPO!
    private String departamento; // <-- ¡NUEVO CAMPO!
    private int cp;
    private LocalidadDTO localidad;

}