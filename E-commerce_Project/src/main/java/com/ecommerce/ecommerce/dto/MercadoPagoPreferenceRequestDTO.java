package com.ecommerce.ecommerce.dto;

import lombok.Data;
import java.util.List;

@Data
public class MercadoPagoPreferenceRequestDTO {
    private String userId; // ID del usuario de tu sistema
    private String buyerPhoneNumber;
    private String shippingAddress;
    private String shippingOption; // "delivery" o "pickup"
    private Double shippingCost;
    private Double totalAmount; // Monto total de la orden
    private List<MercadoPagoItemDTO> items;
    // Puedes añadir más campos de tu CreateOrdenCompraDTO si los necesitas en el backend para la orden completa
    // Por ejemplo, detalles de la dirección si es nueva, etc.
}