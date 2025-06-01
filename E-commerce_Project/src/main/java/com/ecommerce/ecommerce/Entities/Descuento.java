package com.ecommerce.ecommerce.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference; // <-- Importar JsonManagedReference
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

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
    @Column(name="precio_promocional")
    private Double precioPromocional;

    // Relación Descuento <-> Producto (lado dueño)
    @ManyToMany
    @JoinTable(name = "producto_descuentosid", joinColumns = @JoinColumn(name = "descuentoId"), inverseJoinColumns = @JoinColumn(name="productoId"))
    @Builder.Default
    // CAMBIO CRÍTICO: Usar @JsonManagedReference con un nombre para emparejar
    @JsonManagedReference("descuento-productos") // <--- ¡CAMBIO AQUÍ!
    private Set<Producto> productos = new HashSet<>();
}