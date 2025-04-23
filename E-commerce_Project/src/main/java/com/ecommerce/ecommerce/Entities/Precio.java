package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;


@Entity
@Table(name = "Precios")

public class Precio extends Base {

    @Column(name = "precio_compra")
    private Double purchasePrice;
    @Column(name = "precio_venta")
    private Double salePrice;

}
