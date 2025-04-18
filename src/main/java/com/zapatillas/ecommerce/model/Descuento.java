package com.zapatillas.ecommerce.model;

import javax.persistence.*;

@Entity
public class Descuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double porcentaje;

    // Constructor, getters y setters
    public Descuento() {
    }

    public Descuento(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    // Función para aplicar el descuento a un precio base
    public double aplicarDescuento(double precioBase) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo");
        }
        return precioBase * (1 - (porcentaje / 100));
    }

    // Función para verificar si el descuento es válido (mayor que 0)
    public boolean esDescuentoValido() {
        return porcentaje > 0;
    }
}
