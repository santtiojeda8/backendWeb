package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Rol;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("clientes")
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Usuario{

    @ManyToMany(mappedBy = "clientes")
    @Builder.Default
    private List<Direccion> direcciones= new ArrayList<>();
}