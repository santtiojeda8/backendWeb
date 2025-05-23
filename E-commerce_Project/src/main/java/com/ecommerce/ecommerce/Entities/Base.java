package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.Column; // <-- Añade esta importación
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder; // Asegúrate de que esta importación exista

import java.io.Serializable;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Base implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    // Nuevo atributo para el borrado lógico (soft delete)
    @Column(name = "activo") // Opcional: define el nombre de la columna en la BD
    @Builder.Default // Para que el valor por defecto sea 'true' al construir con @SuperBuilder
    protected boolean activo = true;
}