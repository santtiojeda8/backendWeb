package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String denominacion;
    private Double precioOriginal;
    private Double precioFinal;
    private List<String> categorias;
    private Sexo sexo;
    private boolean tienePromocion;
    private List<String> imagenes;
}