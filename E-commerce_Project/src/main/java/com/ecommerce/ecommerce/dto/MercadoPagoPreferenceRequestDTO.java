package com.ecommerce.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MercadoPagoPreferenceRequestDTO {
    private List<MercadoPagoItemRequestDTO> items;
    private String external_reference;
    private Long userId;
    private String payerName;
    private String payerLastName;
    private String payerEmail;
    private String buyerPhoneNumber;
    private String shippingOption;
    private BigDecimal shippingCost;
    private Map<String, String> back_urls;
    private String auto_return;
    private BigDecimal montoTotal;
    private Long direccionId;
    private DireccionDTO nuevaDireccion;

    // ****** ¡ESTA ES LA LÍNEA QUE DEBES CAMBIAR! ******
    private List<CreateOrdenCompraDetalleDTO> detalles; // <-- ¡Cámbialo a CreateOrdenCompraDetalleDTO!
    // *************************************************

    private String notification_url;
    private String direccionEnvio;
}