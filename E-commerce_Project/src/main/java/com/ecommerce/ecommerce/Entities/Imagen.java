package com.ecommerce.ecommerce.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "imagen")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Imagen extends Base{

    @Column(name = "url")
    private String url;

    @ManyToOne // Una imagen pertenece a UN producto (o un usuario, en tu caso)
    @JoinColumn(name = "producto_id") // Nombre de la columna FK en la tabla 'imagen' (si es de producto)
    @JsonBackReference("producto-imagenes") // <--- ¡CAMBIO AQUÍ! Debe coincidir con el nombre en Producto
    private Producto producto; // Campo que mapea al Producto asociado a esta imagen
}