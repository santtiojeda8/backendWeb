package com.ecommerce.ecommerce.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference; // <-- Importar JsonBackReference

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="categorias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Categoria extends Base{
    @Column(name="denominacion")
    private String denominacion;

    // Relación Categoria <-> Categoria (Padre-Hijo)
    @ManyToOne
    @JoinColumn(name = "categoria_padre_id")
    @JsonBackReference("categoria-parent-child") // <-- Añadir JsonBackReference (con un nombre opcional para desambiguar si hay varios ciclos)
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference("categoria-parent-child") // <-- Asegúrate de que el nombre coincida con el lado BackReference
    private Set<Categoria> subcategorias = new HashSet<>();

    // Relación Categoria <-> Producto
    @ManyToMany(mappedBy = "categorias") // Mapea al campo 'categorias' en la entidad Producto
    @Builder.Default
    @JsonBackReference("producto-categorias") // <-- ¡CAMBIAR A JsonBackReference! (con un nombre para coincidir con el lado Managed)
    private Set<Producto> productos = new HashSet<>();

}