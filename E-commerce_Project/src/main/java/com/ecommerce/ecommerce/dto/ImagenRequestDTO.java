package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagenRequestDTO {
    private Long id; // Null para nuevas imágenes, presente para imágenes existentes
    private String url; // La URL o denominación de la imagen
    // Si necesitas más campos como 'orden', 'altText', etc., agrégalos aquí
    private boolean activo; // Si quieres poder activar/desactivar imágenes
}