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
    private Double total;
    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;
    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdenCompraDetalle> detalles = new HashSet<>();
}
