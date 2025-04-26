package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Table(name="descuento")
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
    @Column(name="precio_promocional")
    private Double precioPromocional;

    @ManyToOne
    @JoinColumn(name = "producto_id") // Nombre de la columna en tabla 'descuento'
    private Producto producto;
}

