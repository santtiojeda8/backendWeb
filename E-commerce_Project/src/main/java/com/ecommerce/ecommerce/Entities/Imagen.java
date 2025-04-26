package com.ecommerce.ecommerce.Entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    @Column(name = "url_imagen")
    private String urlImagen;


}
