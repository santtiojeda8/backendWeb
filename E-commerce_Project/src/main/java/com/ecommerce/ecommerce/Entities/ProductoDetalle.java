package com.ecommerce.ecommerce.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference; // <-- Importa esta anotación
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

    // --- CAMBIO AQUÍ: Ahora Color es una entidad ManyToOne ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false) // Columna de la clave foránea
    private Color color; // Referencia a la entidad Color

    // --- CAMBIO AQUÍ: Ahora Talle es una entidad ManyToOne ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talle_id", nullable = false) // Columna de la clave foránea
    private Talle talle; // Referencia a la entidad Talle

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonBackReference("producto-detalles") // <-- ¡AGREGA ESTO! El nombre debe coincidir con el de Producto
    private Producto producto;
}