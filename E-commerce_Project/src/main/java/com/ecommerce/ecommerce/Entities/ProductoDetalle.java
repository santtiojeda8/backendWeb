package com.ecommerce.ecommerce.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonIgnore; // ELIMINA ESTO DE AQUÍ
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "producto_detalle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductoDetalle extends Base {

    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "stock_actual")
    private Integer stockActual;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @ManyToOne(fetch = FetchType.EAGER) // Cambiado a EAGER para asegurar carga. Si funciona, puedes optimizar.
    @JoinColumn(name = "color_id", nullable = false)
    private Color color; // Referencia a la entidad Color

    @ManyToOne(fetch = FetchType.EAGER) // Cambiado a EAGER para asegurar carga. Si funciona, puedes optimizar.
    @JoinColumn(name = "talle_id", nullable = false)
    private Talle talle; // Referencia a la entidad Talle

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonBackReference("producto-detalles")
    private Producto producto;
}