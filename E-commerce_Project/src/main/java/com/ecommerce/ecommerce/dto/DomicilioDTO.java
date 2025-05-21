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
    private String piso; // Nuevo campo
    private String departamento; // Nuevo campo
    private int cp; // Código Postal
    private String localidadNombre; // Nombre de la localidad
    private String provinciaNombre; // Nombre de la provincia
    // No necesitamos el ID de localidad/provincia aquí, solo el nombre para mostrar en el frontend
}