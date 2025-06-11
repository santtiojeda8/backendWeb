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
@Table(name = "talle") // Define la tabla para la entidad Talle
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Talle extends Base {

    @Column(name = "nombre_talle", unique = true, nullable = false)
    private String nombreTalle; // Por ejemplo: "XS", "M", "TALLE_38"
}
