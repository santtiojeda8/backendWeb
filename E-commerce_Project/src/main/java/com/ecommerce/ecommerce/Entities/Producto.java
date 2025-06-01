package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <-- ¡Eliminar esta importación si no se usa!

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "productos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
// ELIMINAR esta anotación, ya que usas DTOs de solicitud para POST/PUT
// @JsonIgnoreProperties(value = {"categorias", "descuentos"}, allowGetters = true)
public class Producto extends Base{
    @Column(name = "denominacion")
    protected String denominacion;
    @Column(name = "precio_venta")
    protected Double precioVenta;
    @Enumerated(EnumType.STRING)
    @Column(name = "sexo_producto", length = 20)
    protected Sexo sexo;
    @Column(name = "tiene_promocion")
    private boolean tienePromocion;

    // Relación Producto <-> Categoria (ManyToMany)
    @ManyToMany
    @JoinTable(
            name = "producto_categoria",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    @JsonManagedReference("producto-categorias") // Mantener para la serialización (GET)
    private Set<Categoria> categorias = new HashSet<>();


    // Relación Producto <-> Imagen (OneToMany) - Estas ya están bien configuradas
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference("producto-imagenes") // <-- ¡IMPORTANTE! Añadir un nombre para evitar conflictos
    protected Set<Imagen> imagenes = new HashSet<>();


    // Relación Producto <-> ProductoDetalle (OneToMany) - Estas ya están bien configuradas
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference("producto-detalles") // <-- ¡IMPORTANTE! Añadir un nombre para evitar conflictos
    private Set<ProductoDetalle> productos_detalles = new HashSet<>();

    // Relación Producto <-> Descuento (ManyToMany)
    @ManyToMany(mappedBy = "productos")
    @Builder.Default
    @JsonBackReference("descuento-productos") // Mantener para la serialización (GET)
    private Set<Descuento> descuentos = new HashSet<>();
}