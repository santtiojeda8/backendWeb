package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.EstadoOrdenCompra;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
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
    private BigDecimal total;

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
    private BigDecimal costoEnvio;

    @ManyToOne(fetch = FetchType.EAGER) // <--- ¡CAMBIA ESTO A EAGER!
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
        }
    }

    public void removeDetalle(OrdenCompraDetalle detalle) {
        if (detalle != null) {
            detalles.remove(detalle);
            detalle.setOrdenCompra(null);
        }
    }

    public void recalcularTotal() {
        this.total = BigDecimal.ZERO;
        if (this.detalles != null) {
            for (OrdenCompraDetalle detalle : detalles) {
                if (detalle.getSubtotal() != null) {
                    this.total = this.total.add(detalle.getSubtotal());
                }
            }
        }
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