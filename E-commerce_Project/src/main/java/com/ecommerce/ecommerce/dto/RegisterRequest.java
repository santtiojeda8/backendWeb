package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    String firstname;
    String lastname;
    String email; // Para Correo Electronico (también lo usaremos como nombre de usuario para login)
    Sexo sexo;
    String password;
}