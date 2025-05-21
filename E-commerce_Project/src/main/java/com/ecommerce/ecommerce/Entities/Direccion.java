package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="direcciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Direccion extends Base{
    @Column(name="calle")
    private String calle;
    @Column(name="numero")
    private int numero;

    // --- NUEVOS CAMPOS ---
    @Column(name="piso") // <-- NUEVO CAMPO
    private String piso; // Cambiado a String para permitir "Planta Baja", "1er piso", etc.
    @Column(name="departamento") // <-- NUEVO CAMPO
    private String departamento; // Cambiado a String
    // --- FIN NUEVOS CAMPOS ---

    @Column(name="cp")
    private int cp; // Codigo Postal

    @ManyToOne // Una Dirección pertenece a UNA Localidad
    @JoinColumn(name="localidad_id") // Nombre de la columna de clave foránea en la tabla 'direcciones'
    private Localidad localidad;

    @ManyToOne(fetch = FetchType.LAZY) // Usar LAZY fetch es una buena práctica
    @JoinColumn(name = "usuario_id") // Nombre de la columna de clave foránea en la tabla 'direcciones' que referencia a 'usuarios'
    private Usuario usuario; // Campo que mapea al Usuario asociado a esta dirección

}