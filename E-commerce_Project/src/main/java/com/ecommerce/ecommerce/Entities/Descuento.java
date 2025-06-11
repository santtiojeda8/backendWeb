package com.ecommerce.ecommerce.Entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;;
import java.math.BigDecimal; // Recomendado para precioPromocional si es un factor (0.00-1.00)

@Entity
@Table(name="descuentos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Descuento extends Base{
    @Column(name="denominacion")
    private String denominacion;
    @Column(name="fecha_desde")
    private LocalDate fechaDesde;
    @Column(name="fecha_hasta")
    private LocalDate fechaHasta;
    @Column(name="hora_desde")
    private LocalTime horaDesde;
    @Column(name="hora_hasta")
    private LocalTime horaHasta;
    @Column(name="descripcion_descuento")
    private String descripcionDescuento;

    @Column(name="precio_promocional", columnDefinition = "DECIMAL(5,2)") // Ejemplo: 0.00 a 99.99
    private BigDecimal precioPromocional; // CAMBIO A BIGDECIMAL

}