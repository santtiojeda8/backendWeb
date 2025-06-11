package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

// Importa DomicilioDTO si no lo tienes ya, y asegúrate de que exista


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateDTO {
    private String email;

    private String firstname;
    private String lastname;

    // Considera seriamente si el email debe ser actualizable aquí.
    // Si no, elimínalo. Si sí, asegúrate de procesarlo en el servicio.
    // private String email; // QUITA ESTA LÍNEA SI NO VAS A ACTUALIZAR EL EMAIL

    private Integer dni;

    private Sexo sexo;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    private String telefono;

    // Asegúrate de que DomicilioDTO tiene los mismos campos que tu entidad Direccion.
    // Y que tiene un 'id' para direcciones existentes.
    private List<DireccionDTO> addresses; // Para enviar todas las direcciones actualizadas
}