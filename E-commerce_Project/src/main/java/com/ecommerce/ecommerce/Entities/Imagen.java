package com.ecommerce.ecommerce.Entities;

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

    @Column(name = "denominacion") // Este campo guardará la URL completa (ej. http://localhost:8080/uploads/uuid.png)
    private String denominacion;

    @ManyToOne // Una imagen pertenece a UN producto (o un usuario, en tu caso)
    @JoinColumn(name = "producto_id") // Nombre de la columna FK en la tabla 'imagen' (si es de producto)
    @JsonBackReference("producto-imagenes") // <--- ¡CAMBIO AQUÍ! Debe coincidir con el nombre en Producto
    private Producto producto; // Campo que mapea al Producto asociado a esta imagen
}