package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENTE")  // Asigna un valor discriminador para identificar los Clientes
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente extends Usuario {

    @Column(name = "rol")
    private Rol rol;

    @ManyToMany(mappedBy = "clientes")
    private List<Direccion> direcciones = new ArrayList<>();
}
