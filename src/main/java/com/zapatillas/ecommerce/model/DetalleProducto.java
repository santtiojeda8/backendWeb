package com.zapatillas.ecommerce.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Entity
public class DetalleProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Producto producto;

    @OneToMany(mappedBy = "detalleProducto")
    private List<Imagen> imagenes;

    @ManyToMany(mappedBy = "detalleProductos")
    private List<Talle> talles;

    @OneToMany(mappedBy = "detalleProducto")
    private List<Precio> precios;

    @OneToMany(mappedBy = "detalleProducto")
    private List<Descuento> descuentos;

    private String color;
    private Integer stock;

    // Constructor, getters y setters
    public DetalleProducto() {
    }

    public DetalleProducto(Producto producto, String color, Integer stock) {
        this.producto = producto;
        this.color = color;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Imagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<Imagen> imagenes) {
        this.imagenes = imagenes;
    }

    public List<Talle> getTalles() {
        return talles;
    }

    public void setTalles(List<Talle> talles) {
        this.talles = talles;
    }

    public List<Precio> getPrecios() {
        return precios;
    }

    public void setPrecios(List<Precio> precios) {
        this.precios = precios;
    }

    public List<Descuento> getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(List<Descuento> descuentos) {
        this.descuentos = descuentos;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    // 🔹 Función: obtener el precio más reciente (usando fechaInicio si existe en tu clase Precio)
    public Precio obtenerPrecioActual() {
        if (precios == null || precios.isEmpty()) return null;
        return precios.stream()
                .sorted(Comparator.comparing(Precio::getFechaInicio).reversed())
                .findFirst()
                .orElse(null);
    }

    // 🔹 Función: obtener el descuento más reciente (opcional)
    public Descuento obtenerDescuentoActual() {
        if (descuentos == null || descuentos.isEmpty()) return null;
        return descuentos.get(0); // o aplicar lógica similar a precios si hay fechas
    }

    // 🔹 Función: calcular el precio final con descuento aplicado
    public BigDecimal calcularPrecioConDescuento() {
        Precio precioActual = obtenerPrecioActual();
        if (precioActual == null) return BigDecimal.ZERO;

        BigDecimal precio = BigDecimal.valueOf(precioActual.getPrecio());
        Descuento descuento = obtenerDescuentoActual();

        if (descuento != null && descuento.getPorcentaje() > 0) {
            BigDecimal descuentoAplicado = precio
                    .multiply(BigDecimal.valueOf(descuento.getPorcentaje()))
                    .divide(BigDecimal.valueOf(100));
            return precio.subtract(descuentoAplicado);
        }

        return precio;
    }
}
