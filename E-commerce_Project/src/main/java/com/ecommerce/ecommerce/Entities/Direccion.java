// com.ecommerce.ecommerce.Entities.Direccion.java
package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="direcciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true) // Necesario para que EqualsAndHashCode considere los campos de la clase padre (Base)
public class Direccion extends Base { // Hereda 'activo' de Base
    @Column(name="calle")
    private String calle;
    @Column(name="numero")
    private int numero;

    @Column(name="piso")
    private String piso;
    @Column(name="departamento")
    private String departamento;

    @Column(name="cp")
    private int cp; // Codigo Postal

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="localidad_id")
    private Localidad localidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}