package com.ecommerce.ecommerce.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name="localidades")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Localidad extends Base{
    @Column(name="localidad")
    private String nombre;
    @ManyToOne
    @JoinColumn(name="provincia_id")
    private Provincia provincia;
}

