package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    // No incluir contraseñas o datos sensibles aquí
}