package com.ecommerce.ecommerce.dto;
import com.ecommerce.ecommerce.Entities.Talle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

// Hereda de BaseDTO si tienes una, o directamente define las propiedades
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TalleDTO {
    private Long id;
    private String nombreTalle;
    private boolean activo; // Para manejar el estado de borrado lógico
    // Constructor para mapear desde la entidad Talle
    public TalleDTO(Talle entity) {
        this.id = entity.getId();
        this.nombreTalle = entity.getNombreTalle();
        this.activo = entity.isActivo();
    }
}
