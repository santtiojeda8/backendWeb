package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Color;
import com.ecommerce.ecommerce.Entities.enums.Talle;
// Asegúrate de importar JsonBackReference
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="producto_detalle")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductoDetalle extends Base{
    @ManyToOne // Un detalle de producto pertenece a UN producto
    @JoinColumn(name = "producto_id") // Nombre de la columna FK en la tabla 'producto_detalle'
    // <-- AÑADIR ESTA ANOTACIÓN JsonBackReference con un nombre que coincida con el lado Managed en Producto
    @JsonBackReference("producto-detalles")
    private Producto producto; // Campo que mapea al Producto asociado a este detalle

    @Column(name = "precio_compra")
    private Double precioCompra;
    @Column(name = "stock_actual")
    private Integer stockActual;
    @Column(name = "cantidad")
    protected Integer cantidad; // Revisa si este campo 'cantidad' aquí es correcto o si se usa en OrdenCompraDetalle
    @Column(name = "stock_maximo")
    private Integer stockMaximo;
    @Enumerated(EnumType.STRING)
    @Column(name = "color", length = 20)
    private Color color;
    @Enumerated(EnumType.STRING)
    @Column(name = "talle", length = 20)
    private Talle talle;

    // Nota: Si OrdenCompraDetalle tiene una relación @ManyToOne con ProductoDetalle,
    // y ProductoDetalle tuviera una colección de OrdenCompraDetalle, también necesitarían anotaciones Managed/Back.
    // Basado en el código de OrdenCompraDetalle que me pasaste, parece que la relación es unidireccional desde OrdenCompraDetalle a ProductoDetalle.

}