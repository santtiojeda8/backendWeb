package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orden_compra_detalle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Agregamos @EntityListeners para los callbacks de ciclo de vida si usas listeners externos,
// pero para métodos dentro de la propia entidad, @PrePersist y @PreUpdate son suficientes.
public class OrdenCompraDetalle extends Base {

    // @ManyToOne es el lado propietario de la relación, la columna 'orden_compra_id'
    // está definida aquí, lo cual es correcto.
    @ManyToOne(fetch = FetchType.LAZY) // Usar LAZY fetch es una buena práctica para ManyToOne
    @JoinColumn(name = "orden_compra_id")
    private OrdenCompra ordenCompra;

    @ManyToOne(fetch = FetchType.LAZY) // Usar LAZY fetch
    @JoinColumn(name = "productodetalle_id")
    private ProductoDetalle productoDetalle;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "subtotal")
    private Double subtotal; // Este campo se calculará automáticamente

    // Método para calcular el subtotal (ya existía)
    public Double calcularSubtotal() {
        // Asegurarse de que productoDetalle y su precioCompra no sean nulos
        if (this.productoDetalle != null && this.productoDetalle.getPrecioCompra() != null && this.cantidad != null) {
            return this.productoDetalle.getPrecioCompra() * this.cantidad;
        }
        return 0.0; // Retorna 0.0 si no se puede calcular
    }

    // --- Callbacks de Ciclo de Vida JPA ---

    // Se ejecuta antes de que la entidad sea persistida (insertada)
    @PrePersist
    public void prePersist() {
        this.subtotal = calcularSubtotal();
        // Opcional: Si quieres actualizar el total de la orden padre inmediatamente
        // Esto puede causar problemas si la orden padre aún no está persistida o gestionada
        // Es más seguro manejar la actualización del total en la entidad OrdenCompra
    }

    // Se ejecuta antes de que la entidad sea actualizada
    @PreUpdate
    public void preUpdate() {
        this.subtotal = calcularSubtotal();
        // Opcional: Si quieres actualizar el total de la orden padre inmediatamente
        // Es más seguro manejar la actualización del total en la entidad OrdenCompra
    }

    // --- Métodos para gestionar la relación bidireccional ---
    // Es una buena práctica tener estos métodos en ambos lados de la relación bidireccional
    // aunque el lado ManyToOne (este) es el propietario.

    public void setOrdenCompra(OrdenCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public void setProductoDetalle(ProductoDetalle productoDetalle) {
        this.productoDetalle = productoDetalle;
    }
}
