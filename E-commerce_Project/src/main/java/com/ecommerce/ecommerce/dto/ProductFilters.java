package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import java.util.List;

// Clase para recibir los parámetros de filtro y ordenamiento en el body de la solicitud POST.
public class ProductFilters {
    private String denominacion;
    private List<String> categorias;
    private Sexo sexo; // Usa el Enum Sexo si lo tienes definido
    private Boolean tienePromocion;
    private Double minPrice;
    private Double maxPrice;
    private List<String> colores;
    private List<String> talles;
    private Integer stockMinimo;
    private String orderBy; // Campo por el que ordenar (ej: "denominacion", "precioVenta")
    private String orderDirection; // Dirección del orden ("asc", "desc")

    // Constructor vacío (necesario para @RequestBody)
    public ProductFilters() {
    }

    // Getters y Setters para todos los campos
    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Boolean getTienePromocion() {
        return tienePromocion;
    }

    public void setTienePromocion(Boolean tienePromocion) {
        this.tienePromocion = tienePromocion;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<String> getColores() {
        return colores;
    }

    public void setColores(List<String> colores) {
        this.colores = colores;
    }

    public List<String> getTalles() {
        return talles;
    }

    public void setTalles(List<String> talles) {
        this.talles = talles;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }
}
