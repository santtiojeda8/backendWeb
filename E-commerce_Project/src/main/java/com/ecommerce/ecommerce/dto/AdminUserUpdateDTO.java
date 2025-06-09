package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserUpdateDTO {
    private Boolean activo; // Usamos Boolean para que pueda ser null si no se envía
    private Rol rol;
}