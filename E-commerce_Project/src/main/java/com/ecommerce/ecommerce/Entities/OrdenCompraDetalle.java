package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder; // Añadir si OrdenCompraDetalle también usa SuperBuilder

@Entity
@Table(name = "orden_compra_detalle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Asegúrate de que use SuperBuilder para heredar 'activo' de Base
public class OrdenCompraDetalle extends Base {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id")
    private OrdenCompra ordenCompra;

    // --- CAMBIO CLAVE: Usa ProductoDetalle, NO Producto ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productodetalle_id") // Columna FK a ProductoDetalle
    private ProductoDetalle productoDetalle; // Campo que mapea a ProductoDetalle
    // --- FIN CAMBIO CLAVE ---

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "subtotal")
    private Double subtotal;

    // Método para calcular el subtotal (ahora usando productoDetalle.getPrecioCompra())
    public Double calcularSubtotal() {
        if (this.productoDetalle != null && this.productoDetalle.getPrecioCompra() != null && this.cantidad != null) {
            // Usa getPrecioCompra() de ProductoDetalle
            return this.productoDetalle.getPrecioCompra() * this.cantidad;
        }
        return 0.0;
    }

    @PrePersist
    public void prePersist() {
        this.subtotal = calcularSubtotal();
    }

    @PreUpdate
    public void preUpdate() {
        this.subtotal = calcularSubtotal();
    }

    // Asegurarte de que estos getters/setters estén disponibles (Lombok los genera si @Getter/@Setter está presente)
    // public ProductoDetalle getProductoDetalle() { return productoDetalle; }
    // public void setProductoDetalle(ProductoDetalle productoDetalle) { this.productoDetalle = productoDetalle; }
}