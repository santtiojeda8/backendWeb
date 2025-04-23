package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;


@Entity
@Table(name = "Categoria")


public class Categoria extends Base {
    @Column(name = "nombre")
    private String name;
}
