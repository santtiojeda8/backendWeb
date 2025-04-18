package com.zapatillas.ecommerce.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Precio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double precio;

    private LocalDate fechaInicio;

    @ManyToOne
    private Descuento descuento;

    @ManyToOne
    private DetalleProducto detalleProducto;

    // Constructors
    public Precio() {
    }

    public Precio(double precio, Descuento descuento, LocalDate fechaInicio) {
        this.precio = precio;
        this.descuento = descuento;
        this.fechaInicio = fechaInicio;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Descuento getDescuento() {
        return descuento;
    }

    public void setDescuento(Descuento descuento) {
        this.descuento = descuento;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public DetalleProducto getDetalleProducto() {
        return detalleProducto;
    }

    public void setDetalleProducto(DetalleProducto detalleProducto) {
        this.detalleProducto = detalleProducto;
    }

    // 🔹 Función para calcular el precio final con descuento
    public BigDecimal calcularPrecioConDescuento() {
        BigDecimal precioBase = BigDecimal.valueOf(precio);
        if (descuento != null && descuento.getPorcentaje() > 0) {
            BigDecimal porcentaje = BigDecimal.valueOf(descuento.getPorcentaje()).divide(BigDecimal.valueOf(100));
            return precioBase.subtract(precioBase.multiply(porcentaje));
        }
        return precioBase;
    }
}
