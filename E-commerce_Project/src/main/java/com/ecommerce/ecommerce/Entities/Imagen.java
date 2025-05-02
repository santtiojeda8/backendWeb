package com.ecommerce.ecommerce.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imagen")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Imagen extends Base{

    @Column(name = "denominacion")
    private String denominacion;
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;


}
