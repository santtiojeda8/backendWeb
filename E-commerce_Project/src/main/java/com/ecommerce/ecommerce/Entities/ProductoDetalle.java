package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Color;
import com.ecommerce.ecommerce.Entities.enums.Talle;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private Color color;

    @Enumerated(EnumType.STRING)
    @Column(name = "talle")
    private Talle talle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonBackReference("producto-detalles") // <--- ¡CAMBIO AQUÍ! Debe coincidir con el nombre en Producto
    private Producto producto;
}