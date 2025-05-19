package com.ecommerce.ecommerce.dto;

// Importar las clases necesarias
import com.ecommerce.ecommerce.Entities.enums.Sexo;
import java.util.List;


import com.ecommerce.ecommerce.dto.CategoriaDTO;
import com.ecommerce.ecommerce.dto.ImagenDTO;
import com.ecommerce.ecommerce.dto.ProductoDetalleDTO;


public class ProductoDTO {

    private Long id;
    private String denominacion;
    private Double precioOriginal; // Mapea de precioVenta
    private Double precioFinal;    // Precio calculado con descuentos
    private List<CategoriaDTO> categorias; // <-- AHORA LISTA DE OBJETOS CategoriaDTO
    private Sexo sexo;
    private boolean tienePromocion;
    private List<ImagenDTO> imagenes; // <-- AHORA LISTA DE OBJETOS ImagenDTO
    private List<ProductoDetalleDTO> productos_detalles; // <-- NOMBRE CORREGIDO A 'productos_detalles'

    // Asegúrate de que CategoriaDTO, ImagenDTO y ProductoDetalleDTO
    // estén definidas correctamente con sus campos (id, denominacion, etc.)

    // Constructor vacío
    public ProductoDTO() {
    }

    // Constructor con todos los campos
    public ProductoDTO(Long id, String denominacion, Double precioOriginal, Double precioFinal, List<CategoriaDTO> categorias, Sexo sexo, boolean tienePromocion, List<ImagenDTO> imagenes, List<ProductoDetalleDTO> productos_detalles) {
        this.id = id;
        this.denominacion = denominacion;
        this.precioOriginal = precioOriginal;
        this.precioFinal = precioFinal;
        this.categorias = categorias;
        this.sexo = sexo;
        this.tienePromocion = tienePromocion;
        this.imagenes = imagenes;
        this.productos_detalles = productos_detalles;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDenominacion() { return denominacion; }
    public void setDenominacion(String denominacion) { this.denominacion = denominacion; }
    public Double getPrecioOriginal() { return precioOriginal; }
    public void setPrecioOriginal(Double precioOriginal) { this.precioOriginal = precioOriginal; }
    public Double getPrecioFinal() { return precioFinal; }
    public void setPrecioFinal(Double precioFinal) { this.precioFinal = precioFinal; }
    public List<CategoriaDTO> getCategorias() { return categorias; }
    public void setCategorias(List<CategoriaDTO> categorias) { this.categorias = categorias; }
    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }
    public boolean isTienePromocion() { return tienePromocion; }
    public void setTienePromocion(boolean tienePromocion) { this.tienePromocion = tienePromocion; }
    public List<ImagenDTO> getImagenes() { return imagenes; }
    public void setImagenes(List<ImagenDTO> imagenes) { this.imagenes = imagenes; }
    public List<ProductoDetalleDTO> getProductos_detalles() { return productos_detalles; }
    public void setProductos_detalles(List<ProductoDetalleDTO> productos_detalles) { this.productos_detalles = productos_detalles; }

}