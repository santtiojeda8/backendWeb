package com.ecommerce.ecommerce.Entities;

// Asegúrate de importar JsonBackReference
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imagen")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Imagen extends Base{

    @Column(name = "denominacion")
    private String denominacion;

    @ManyToOne // Una imagen pertenece a UN producto
    @JoinColumn(name = "producto_id") // Nombre de la columna FK en la tabla 'imagen'

    @JsonBackReference("producto-imagenes")
    private Producto producto; // Campo que mapea al Producto asociado a esta imagen
 }


