package com.ecommerce.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraDTO {
    private Long id;
    private Double total;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Asegura el formato ISO 8601
    private LocalDateTime fechaCompra;

    private String direccionEnvio; // O un DomicilioDTO si es más complejo
    private List<OrdenCompraDetalleDTO> detalles;
     private UserDTO usuario;
}