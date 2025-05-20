package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "El nombre no puede estar vacío.") // No nulo y no solo espacios en blanco
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    String firstname;

    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres.")
    String lastname;

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "El formato del correo electrónico no es válido.") // Valida formato de email
    @Size(max = 100, message = "El correo electrónico no puede exceder los 100 caracteres.")
    String email;

    @NotNull(message = "El sexo no puede ser nulo.") // Es un enum, así que NotNull es adecuado
    Sexo sexo;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    // Considera añadir validaciones más complejas (regex para mayúsculas, números, símbolos) si lo necesitas.
    String password;
    @NotNull(message = "El DNI no puede estar vacío.")
    @Digits(integer = 8, fraction = 0, message = "El DNI debe ser un número entero de hasta 8 dígitos.")
    @Min(value = 1000000, message = "El DNI debe tener al menos 7 dígitos.") // Por ejemplo, un DNI válido suele tener al menos 7
    private Integer dni; // Usamos Integer para manejar nulos si no es @NotNull. Si es @NotNull, int también serviría.
}
