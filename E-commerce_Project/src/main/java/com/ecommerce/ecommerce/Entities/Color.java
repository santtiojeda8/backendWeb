package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.Base;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "color") // Define la tabla para la entidad Color
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Color extends Base {

    @Column(name = "nombre_color", unique = true, nullable = false)
    private String nombreColor;
}
