// src/main/java/com/ecommerce/ecommerce/dto/DescuentoDTO.java
package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Importar BigDecimal
import java.time.LocalTime; // O Instant, LocalDate, etc., según tu entidad
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoDTO {
    private Long id;
    private String denominacion;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
    private String descripcionDescuento;
    private BigDecimal precioPromocional; // <--- ¡Asegúrate de que exista y sea BigDecimal!
    private boolean activo; // <--- ¡Asegúrate de que exista!
}