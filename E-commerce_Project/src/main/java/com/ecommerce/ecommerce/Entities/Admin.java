package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ADMIN")  // Asigna un valor discriminador para identificar los Admin
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin extends Usuario {

    @Column(name = "rol")
    private Rol rol;

    @Column(name = "activo")
    private Boolean activo;
}
