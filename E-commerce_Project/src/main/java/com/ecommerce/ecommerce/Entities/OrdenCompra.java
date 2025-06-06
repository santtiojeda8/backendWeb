// com.ecommerce.ecommerce.Entities.OrdenCompra.java
package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.EstadoOrdenCompra;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal; // <-- Asegúrate de tener esta importación
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orden_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrdenCompra extends Base {

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total; // <-- ¡Asegúrate de que sea BigDecimal!

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    @Column(name = "fecha_actualizacion_estado")
    private LocalDateTime fechaActualizacionEstado;

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_orden")
    private EstadoOrdenCompra estadoOrden;

    @Column(name = "mercadopago_preference_id")
    private String mercadopagoPreferenceId;

    @Column(name = "mercadopago_payment_id")
    private String mercadopagoPaymentId;

    @Column(name = "telefono_comprador")
    private String telefono;

    @Column(name = "tipo_envio")
    private String tipoEnvio;

    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio; // <-- ¡Asegúrate de que sea BigDecimal!

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_id")
    private Direccion direccion;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference("ordenCompra-detalles")
    private Set<OrdenCompraDetalle> detalles = new HashSet<>();

    public void addDetalle(OrdenCompraDetalle detalle) {
        if (detalle != null) {
            detalles.add(detalle);
            detalle.setOrdenCompra(this);
            // recalcularTotal(); // Ya manejado por @PrePersist/@PreUpdate
        }
    }

    public void removeDetalle(OrdenCompraDetalle detalle) {
        if (detalle != null) {
            detalles.remove(detalle);
            detalle.setOrdenCompra(null);
            // recalcularTotal(); // Ya manejado por @PrePersist/@PreUpdate
        }
    }

    // --- CORRECCIÓN CLAVE AQUÍ ---
    public void recalcularTotal() {
        this.total = BigDecimal.ZERO; // Siempre inicializar con BigDecimal.ZERO
        if (this.detalles != null) {
            for (OrdenCompraDetalle detalle : detalles) {
                // Ahora detalle.getSubtotal() debe devolver BigDecimal
                if (detalle.getSubtotal() != null) {
                    this.total = this.total.add(detalle.getSubtotal());
                }
            }
        }
        // Ahora this.costoEnvio debe ser BigDecimal
        if (this.costoEnvio != null) {
            this.total = this.total.add(this.costoEnvio);
        }
    }

    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        recalcularTotal();
        this.fechaActualizacionEstado = LocalDateTime.now();
        if (this.fechaCompra == null) {
            this.fechaCompra = LocalDateTime.now();
        }
    }
}