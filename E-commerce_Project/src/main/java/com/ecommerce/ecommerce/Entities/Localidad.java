package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;


@Entity
@Table(name = "Localidad")

public class Localidad extends Base {

    @Column(name = "nombre")
    private String name;
}
