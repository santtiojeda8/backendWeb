package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTO {
    private Long id;
    private String denominacion;
    private List<CategoriaDTO> subcategorias = new ArrayList<>(); // Soporte para subcategorías

    public CategoriaDTO(Long id, String denominacion) {
        this.id = id;
        this.denominacion = denominacion;
    }

    public void addSubcategoria(CategoriaDTO subcategoria) {
        if (this.subcategorias == null) {
            this.subcategorias = new ArrayList<>();
        }
        this.subcategorias.add(subcategoria);
    }
}