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
    @ManyToOne
    @JoinColumn(name="categoriaId")
    protected Categoria categoria;
    @OneToMany
    @JoinColumn(name = "imagenesArticuloId")
    protected Set<Imagen> imagenes=new HashSet<>();
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductoDetalle> productos_detalles = new HashSet<>();

}
