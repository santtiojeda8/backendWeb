package com.ecommerce.ecommerce.Entities;



import com.ecommerce.ecommerce.Entities.enums.Sexo;
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
public class Producto extends Base{
    @Column(name = "denominacion")
    protected String denominacion;
    @Column(name = "precio_venta")
    protected Double precioVenta;
    @Enumerated(EnumType.STRING)
    @Column(name = "sexo_producto")
    protected Sexo sexo;
    @Column(name = "tiene_promocion")
    private boolean tienePromocion;
    @ManyToMany
    @JoinTable(
            name = "producto_categoria",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "imagenesProductoId")
    protected Set<Imagen> imagenes=new HashSet<>();
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductoDetalle> productos_detalles = new HashSet<>();
    @ManyToMany(mappedBy = "productos")
    private Set<Descuento> descuentos = new HashSet<>();


}
