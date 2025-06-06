// com.ecommerce.ecommerce.Entities.Localidad.java
package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="localidades")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Localidad extends Base { // Hereda 'activo' de Base
    @Column(name="localidad")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY) // Siempre es buena práctica lazy en ManyToOne
    @JoinColumn(name="provincia_id")
    private Provincia provincia;
}