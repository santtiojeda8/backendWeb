package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImagenDTO {
    private Long id;
    private String url; // Corresponds to 'denominacion' in the Imagen entity
    private boolean activo; // Crucial for consistency
}