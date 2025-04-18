/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zapatillas.ecommerce.model;

/**
 *
 * @author astud
 */
import javax.persistence.*;

@Entity
public class Talle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String talla; // Valor del talle, como "S", "M", "L", "42", etc.

    @ManyToOne
    private DetalleProducto detalleProducto;

    private Integer stock; // Stock disponible para esta talla

    // Constructor, getters y setters
    public Talle() {
    }

    public Talle(String talla, DetalleProducto detalleProducto, Integer stock) {
        this.talla = talla;
        this.detalleProducto = detalleProducto;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public DetalleProducto getDetalleProducto() {
        return detalleProducto;
    }

    public void setDetalleProducto(DetalleProducto detalleProducto) {
        this.detalleProducto = detalleProducto;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
