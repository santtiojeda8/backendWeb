package com.ecommerce.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraDTO {
    private Long id;
    private BigDecimal total;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCompra;
    private String direccionEnvio; // This might become less relevant if Direccion entity is fully used
    private List<OrdenCompraDetalleDTO> detalles;
    private Long usuarioId;
    private String estadoOrden; // Represents EstadoOrdenCompra enum as String
    private String mercadopagoPreferenceId;
    private String mercadopagoPaymentId;
    private String shippingOption; // e.g., "delivery", "pickup"
    private BigDecimal shippingCost;
    private String buyerPhoneNumber;
    private Long direccionId; // ID of an existing Direccion
    private DireccionDTO nuevaDireccion; // <--- ADD THIS FIELD
}