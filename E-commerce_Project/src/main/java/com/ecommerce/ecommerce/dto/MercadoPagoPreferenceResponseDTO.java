package com.ecommerce.ecommerce.dto;

public class MercadoPagoPreferenceResponseDTO {
    private String initPoint;


    // Constructor para cuando no hay un initPoint (ej. en caso de error)
    public MercadoPagoPreferenceResponseDTO() {
        this.initPoint = null;
    }

    public MercadoPagoPreferenceResponseDTO(String initPoint) {
        this.initPoint = initPoint;
    }

    // Getter
    public String getInitPoint() {
        return initPoint;
    }
    // Si añades preferenceId, también su getter/setter
}