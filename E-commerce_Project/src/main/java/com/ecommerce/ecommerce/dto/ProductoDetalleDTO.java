package com.ecommerce.ecommerce.dto;

import java.util.List;

// DTO para representar los detalles de un producto (talla, color, stock, etc.).
// Asegúrate de que esta definición coincida con la entidad ProductoDetalle
public class ProductoDetalleDTO {
    private Long id;
    private Double precioCompra;
    private Integer stockActual;
    private Integer cantidad; // Cantidad asociada a este detalle (ej: en un carrito)
    private Integer stockMaximo;
    private String color; // String para el color
    private String talle; // String para la talla

    // Constructor vacío
    public ProductoDetalleDTO() {}

    // Constructor con campos
    public ProductoDetalleDTO(Long id, Double precioCompra, Integer stockActual, Integer cantidad, Integer stockMaximo, String color, String talle) {
        this.id = id;
        this.precioCompra = precioCompra;
        this.stockActual = stockActual;
        this.cantidad = cantidad;
        this.stockMaximo = stockMaximo;
        this.color = color;
        this.talle = talle;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getTalle() { return talle; }
    public void setTalle(String talle) { this.talle = talle; }
}
