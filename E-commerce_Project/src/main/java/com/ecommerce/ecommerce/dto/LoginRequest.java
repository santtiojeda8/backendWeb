package com.ecommerce.ecommerce.dto;

import jakarta.validation.constraints.Email; // Importar de jakarta.validation
import jakarta.validation.constraints.NotBlank; // Importar de jakarta.validation
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "El formato del correo electrónico no es válido.")
    String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    String password;
}