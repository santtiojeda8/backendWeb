package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orden_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompra extends Base {

    @Column(name = "total")
    private Double total; // Este campo se calculará automáticamente

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    // mappedBy indica que la relación es propiedad del campo 'ordenCompra' en la entidad OrdenCompraDetalle
    // cascade = CascadeType.ALL: Las operaciones de persistencia, actualización, etc. se propagan a los detalles.
    // orphanRemoval = true: Si un detalle se desvincula de esta orden, se elimina de la base de datos.
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrdenCompraDetalle> detalles = new HashSet<>();

    // --- Métodos de Ayuda para Gestionar la Relación y el Total ---

    // Método para añadir un detalle a la orden
    public void addDetalle(OrdenCompraDetalle detalle) {
        if (detalle != null) {
            detalles.add(detalle);
            detalle.setOrdenCompra(this); // Establece la relación bidireccional en el lado del detalle
            recalcularTotal(); // Recalcula el total cada vez que se añade un detalle
        }
    }

    // Método para remover un detalle de la orden
    public void removeDetalle(OrdenCompraDetalle detalle) {
        if (detalle != null) {
            detalles.remove(detalle);
            detalle.setOrdenCompra(null); // Rompe la relación bidireccional en el lado del detalle
            recalcularTotal(); // Recalcula el total cada vez que se remueve un detalle
        }
    }

    // Método para recalcular el total sumando los subtotales de los detalles
    // Se llama automáticamente en addDetalle y removeDetalle
    private void recalcularTotal() {
        this.total = 0.0; // Inicializa el total
        if (this.detalles != null) {
            for (OrdenCompraDetalle detalle : detalles) {
                // Asegurarse de que el subtotal del detalle esté calculado
                // Esto debería estar garantizado por los @PrePersist/@PreUpdate en OrdenCompraDetalle
                if (detalle.getSubtotal() != null) {
                    this.total += detalle.getSubtotal();
                }
            }
        }
    }

    // --- Callbacks de Ciclo de Vida JPA (Opcional pero recomendado para asegurar el total) ---
    // Estos callbacks aseguran que el total se recalcule antes de persistir o actualizar la orden,
    // capturando casos donde los detalles puedan haber sido modificados fuera de add/removeDetalle
    // o al cargar la entidad.

    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        recalcularTotal();
    }

    // --- Getter y Setter para total (Lombok los genera, pero si no usas Lombok...)
    // public Double getTotal() { return total; }
    // public void setTotal(Double total) { this.total = total; } // Puede ser útil si necesitas establecerlo manualmente en algún caso muy específico, pero la idea es que se calcule.
}
