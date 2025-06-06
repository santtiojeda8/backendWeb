package com.ecommerce.ecommerce.dto;
import com.ecommerce.ecommerce.Entities.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

// Hereda de BaseDTO si tienes una, o directamente define las propiedades
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ColorDTO {
    private Long id;
    private String nombreColor;
    private boolean activo; // Para manejar el estado de borrado lógico
    // Constructor para mapear desde la entidad Color
    public ColorDTO(Color entity) {
        this.id = entity.getId();
        this.nombreColor = entity.getNombreColor();
        this.activo = entity.isActivo();
    }
}
