package com.ecommerce.ecommerce.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private String category;
}