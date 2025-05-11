package com.ecommerce.ecommerce.Entities;


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
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;


}
