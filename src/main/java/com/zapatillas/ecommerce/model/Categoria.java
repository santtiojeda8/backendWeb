package com.zapatillas.ecommerce.model;

import com.zapatillas.ecommerce.model.Producto;
import javax.persistence.*;
import java.util.List;

@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    // Función para agregar un producto a la categoría
    public void agregarProducto(Producto producto) {
        if (!this.productos.contains(producto)) {
            this.productos.add(producto);
        }
    }

    // Función para eliminar un producto de la categoría
    public void eliminarProducto(Producto producto) {
        this.productos.remove(producto);
    }

    // Función para contar la cantidad de productos en la categoría
    public int contarProductos() {
        return this.productos != null ? this.productos.size() : 0;
    }
}
