package com.ecommerce.ecommerce.dto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MercadoPagoItemDTO {
    private String id;
    private String title;
    private String description;
    private String pictureUrl;
    private String categoryId;
    private Integer quantity;
    private BigDecimal unitPrice; // <-- Debe ser BigDecimal en Java
}