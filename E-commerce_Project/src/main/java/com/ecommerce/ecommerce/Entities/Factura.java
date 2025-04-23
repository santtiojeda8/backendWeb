package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(name = "Factura")

public class Factura extends Base{

    @Column(name = "total")
    private Double total;
    @Column(name = "fecha_compra")
    private LocalDate datePurchase;
}
