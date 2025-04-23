package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;


@Entity
@Table(name = "Imagenes")


public class Imagen extends Base {

    @Column(name = "url")
    private String url;
}
