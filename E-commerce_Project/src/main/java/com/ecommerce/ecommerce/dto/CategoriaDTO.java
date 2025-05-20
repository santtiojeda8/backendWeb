package com.ecommerce.ecommerce.dto;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList; // Importar ArrayList

// DTO para representar la información de una Categoria en la respuesta de la API
public class CategoriaDTO implements Serializable {

    private Long id;
    private String denominacion;
    private List<CategoriaDTO> subcategorias = new ArrayList<>(); // Lista de subcategorías DTO

    // Constructor vacío
    public CategoriaDTO() {
    }

    // Constructor con campos
    public CategoriaDTO(Long id, String denominacion) {
        this.id = id;
        this.denominacion = denominacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public List<CategoriaDTO> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<CategoriaDTO> subcategorias) {
        this.subcategorias = subcategorias;
    }

    // Método para añadir subcategorías (opcional, puede ser útil al mapear)
    public void addSubcategoria(CategoriaDTO subcategoria) {
        this.subcategorias.add(subcategoria);
    }

    // Opcional: Override de toString(), equals(), hashCode()
}
