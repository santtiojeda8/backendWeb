package com.ecommerce.ecommerce.Entities;

// import com.fasterxml.jackson.annotation.JsonManagedReference; // Eliminar si no se usa más
// import com.fasterxml.jackson.annotation.JsonBackReference; // Eliminar si no se usa más
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore; // Asegúrate de que esta esté importada

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @ManyToOne
    @JoinColumn(name = "categoria_padre_id")
    @JsonBackReference("categoria-parent-child") // Este uso de JsonBackReference y ManagedReference está bien para OneToMany/ManyToOne
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference("categoria-parent-child") // Este uso está bien para OneToMany/ManyToOne
    private Set<Categoria> subcategorias = new HashSet<>();

    // Relación Categoria <-> Producto (ManyToMany)
    @ManyToMany(mappedBy = "categorias")
    @Builder.Default
    @JsonIgnore // <-- ¡CAMBIO AQUÍ! Para ManyToMany, usa @JsonIgnore en ambos lados o DTOs.
    private Set<Producto> productos = new HashSet<>();

}