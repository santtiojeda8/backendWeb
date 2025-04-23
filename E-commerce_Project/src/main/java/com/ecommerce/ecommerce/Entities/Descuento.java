package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Descuentos")


public class Descuento extends Base {

    @Column(name = "nombre")
    private String name;
    @Column(name = "fecha_desde")
    private LocalDate DateFrom;
    @Column(name = "fecha_hasta")
    private LocalDate DateTo;
    @Column(name = "tiempo_desde")
    private LocalTime TimeFrom;
    @Column(name = "tiempo_hasta")
    private LocalTime TimeTo;
    @Column(name = "descripcion")
    private String DiscountDescription;
    @Column(name = "promocion_precio")
    private Double PromotionalPrice;
}
