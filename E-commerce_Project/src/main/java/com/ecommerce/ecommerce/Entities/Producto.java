package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.TipoProducto;
import jakarta.persistence.*;


@Entity
@Table(name = "Productos")


public class Producto extends Base {

    @Column(name = "nombre")
    private String name;
    @Column(name = "categoria")
    private Categoria Category;
    @Column(name = "tipo_producto")
    private TipoProducto tipoProducto;
    @Column(name = "sexo")
    private String sex;
}
