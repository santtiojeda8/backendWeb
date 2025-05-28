package com.ecommerce.ecommerce.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder; // Añadir si OrdenCompra también usa SuperBuilder

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orden_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Asegúrate de que use SuperBuilder para heredar 'activo' de Base
public class OrdenCompra extends Base {

    @Column(name = "total")
    private Double total;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    // --- RELACIÓN CON USUARIO ---
    @ManyToOne(fetch = FetchType.LAZY) // Una orden de compra pertenece a un usuario
    @JoinColumn(name = "usuario_id") // Nombre de la columna FK en la tabla 'orden_compra'
    private Usuario usuario; // Campo que mapea al Usuario asociado a esta orden
    // --- FIN RELACIÓN CON USUARIO ---

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference("ordenCompra-detalles") // Añadir un nombre para JsonManagedReference
    private Set<OrdenCompraDetalle> detalles = new HashSet<>();

    // --- Métodos de Ayuda para Gestionar la Relación y el Total ---

    public void addDetalle(OrdenCompraDetalle detalle) {
        if (detalle != null) {
            detalles.add(detalle);
            detalle.setOrdenCompra(this);
            recalcularTotal();
        }
    }

    public void removeDetalle(OrdenCompraDetalle detalle) {
        if (detalle != null) {
            detalles.remove(detalle);
            detalle.setOrdenCompra(null);
            recalcularTotal();
        }
    }

    private void recalcularTotal() {
        this.total = 0.0;
        if (this.detalles != null) {
            for (OrdenCompraDetalle detalle : detalles) {
                if (detalle.getSubtotal() != null) {
                    this.total += detalle.getSubtotal();
                }
            }
        }
    }

    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        recalcularTotal();
    }
}