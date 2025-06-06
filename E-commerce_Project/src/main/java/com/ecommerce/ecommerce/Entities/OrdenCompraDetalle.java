package com.ecommerce.ecommerce.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal; // <--- ¡ASEGÚRATE DE ESTA IMPORTACIÓN!

@Entity
@Table(name = "orden_compra_detalle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrdenCompraDetalle extends Base {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id")
    @JsonBackReference("ordenCompra-detalles")
    private OrdenCompra ordenCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productodetalle_id")
    private ProductoDetalle productoDetalle;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario; // <--- ¡DEBE SER BIGDECIMAL!

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal; // <--- ¡DEBE SER BIGDECIMAL!

    // --- CORRECCIÓN CLAVE AQUÍ ---
    // Este método asegura que el subtotal se calcule correctamente en la entidad
    public BigDecimal calcularSubtotal() {
        if (this.precioUnitario != null && this.cantidad != null) {
            return this.precioUnitario.multiply(new BigDecimal(this.cantidad));
        }
        return BigDecimal.ZERO;
    }

    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        this.subtotal = calcularSubtotal();
        // Asegurarse de que precioUnitario no sea null antes de calcular
        // (ya lo validamos en calcularSubtotal, pero es una buena práctica)
        if (this.precioUnitario == null && this.productoDetalle != null && this.productoDetalle.getPrecioCompra() != null) {
            this.precioUnitario = this.productoDetalle.getPrecioCompra();
        }
    }
}