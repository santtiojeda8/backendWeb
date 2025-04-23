package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;


// Agregar @GetMapping()

@Entity
@Table(name = "Direccion")

public class Direccion extends Base {

    @Column(name = "calle")
    private String street;
    @Column(name = "numero")
    private Integer number ;
    @Column(name = "codigo_postal")
    private Integer cp;
}
