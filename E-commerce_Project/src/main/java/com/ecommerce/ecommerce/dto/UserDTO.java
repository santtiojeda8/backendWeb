package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Rol;
import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private Integer dni;
    private Sexo sexo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    private String telefono;
    private Rol role;
    private ImagenDTO imagenUser;
    private List<DomicilioDTO> addresses;
}