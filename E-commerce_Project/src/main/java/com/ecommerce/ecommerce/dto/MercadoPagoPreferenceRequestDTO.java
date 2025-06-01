package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MercadoPagoPreferenceRequestDTO {

    private List<MercadoPagoItemDTO> items;
    private String shippingOption;
    private BigDecimal shippingCost; // <-- Debe ser BigDecimal
    private String buyerPhoneNumber;
    private String payerName;
    private String payerLastName;
    private String payerEmail;

    // --- CAMPOS PARA LA ORDEN DE COMPRA ---
    private Long userId;
    private String shippingAddress;
    private Long direccionId;
    private DireccionDTO nuevaDireccion;
    private BigDecimal montoTotal;      // <-- Debe ser BigDecimal
    // --- FIN CAMPOS PARA LA ORDEN DE COMPRA ---

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BackUrlsDTO {
        private String success;
        private String failure;
        private String pending;
    }
}