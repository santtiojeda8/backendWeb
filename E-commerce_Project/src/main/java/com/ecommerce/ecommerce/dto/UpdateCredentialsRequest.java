package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern; // Para validaciones de complejidad de contraseña (opcional)

@Data // Genera getters, setters, toString, equals, hashCode
@Builder // Permite construir objetos con el patrón Builder
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class UpdateCredentialsRequest {

    // Si el email actual es necesario para la operación (ej. para identificar al usuario si cambia de ID o email)
    // No es estrictamente necesario si ya identificas al usuario por el token (authentication.getName())
    // Pero si lo recibes, debe ser válido. No se le pone @NotBlank si es opcional.
    @Email(message = "El email actual debe tener un formato válido")
    @Size(max = 100, message = "El email actual no puede exceder los 100 caracteres.") // Asegúrate de la longitud
    private String currentEmail; // Puede ser null si no se envía

    // La contraseña actual siempre debe ser requerida para un cambio de credenciales por seguridad
    @NotBlank(message = "La contraseña actual no puede estar vacía.")
    private String currentPassword;

    // La nueva contraseña debe tener las mismas o más estrictas validaciones que el registro
    @NotBlank(message = "La nueva contraseña no puede estar vacía.")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres.")


    private String newPassword;

    // El nuevo email debe tener las mismas validaciones que el registro
    // Nota: @NotBlank solo se aplica si el campo *siempre* debe enviarse.
    // Si el nuevo email es OPCIONAL (es decir, a veces solo se cambia la contraseña), no uses @NotBlank.
    // Si se envía, debe ser válido.
    @Email(message = "El nuevo email debe tener un formato válido.")
    @Size(max = 100, message = "El nuevo email no puede exceder los 100 caracteres.")
    private String newEmail; // Puede ser null si no se envía
}