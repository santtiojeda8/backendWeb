package com.ecommerce.ecommerce.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

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
    @JsonBackReference("categoria-parent-child") // Mantener con nombre
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference("categoria-parent-child") // Mantener con nombre
    private Set<Categoria> subcategorias = new HashSet<>();

    // Relación Categoria <-> Producto (ManyToMany)
    @ManyToMany(mappedBy = "categorias")
    @Builder.Default
    @JsonBackReference("producto-categorias") // <--- ¡ASEGÚRATE DE QUE TIENE ESTE MISMO NOMBRE!
    private Set<Producto> productos = new HashSet<>();

}