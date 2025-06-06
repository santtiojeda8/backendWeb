// com.ecommerce.ecommerce.Entities.Provincia.java
package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="provincias")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Provincia extends Base { // Hereda 'activo' de Base
    @Column(name="provincia")
    private String nombre;
}