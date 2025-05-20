package com.ecommerce.ecommerce.dto;

import java.io.Serializable;

public class ImagenDTO implements Serializable {

    private Long id; // Usamos Long para IDs en Java
    private String denominacion; // Contiene la URL de la imagen

    // Constructor vacío (necesario para muchos frameworks como Spring)
    public ImagenDTO() {
    }

    // Constructor con campos
    public ImagenDTO(Long id, String denominacion) {
        this.id = id;
        this.denominacion = denominacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }


}