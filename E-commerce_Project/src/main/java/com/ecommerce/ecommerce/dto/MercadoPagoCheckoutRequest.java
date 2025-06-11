package com.ecommerce.ecommerce.dto;
import lombok.Data; // Asumo que usas Lombok para @Data, @Getter, @Setter
import java.math.BigDecimal;
import java.util.List;

@Data // Genera getters, setters, toString, equals y hashCode
public class MercadoPagoCheckoutRequest {
    private List<CartItemDTO> cartItems;
    private String shippingOption; // "delivery" o "pickup"
    private BigDecimal shippingCost; // Costo de envío

    // Datos del pagador que vienen del frontend
    private String payerName;
    private String payerLastName;
    private String payerEmail;
    private String buyerPhoneNumber;

    // URLs de retorno (pueden venir del frontend o ser hardcodeadas en el backend)
    private String successUrl;
    private String pendingUrl;
    private String failureUrl;
}