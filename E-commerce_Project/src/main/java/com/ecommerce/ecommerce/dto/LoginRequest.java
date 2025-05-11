package com.ecommerce.ecommerce.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {


    String email; // Usaremos el email para el inicio de sesión (coherente con RegisterRequest actualizado)
    String password; // Para Contraseña

}