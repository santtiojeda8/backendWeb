package com.ecommerce.ecommerce.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // ELIMINAR @Column(name="activo") de aquí, ya se hereda de Base

    @ManyToOne(fetch = FetchType.LAZY) // Se recomienda LAZY para ManyToOne
    @JoinColumn(name = "categoria_padre_id", nullable = true) // <-- ¡AÑADIR nullable = true AQUÍ!
    @JsonBackReference("categoria-parent-child")
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL, orphanRemoval = true) // Añadido orphanRemoval=true (buena práctica)
    @Builder.Default
    @JsonManagedReference("categoria-parent-child")
    private Set<Categoria> subcategorias = new HashSet<>();

    // Relación Categoria <-> Producto (ManyToMany)
    @ManyToMany(mappedBy = "categorias")
    @Builder.Default
    @JsonIgnore // Mantengo JsonIgnore, asumiendo que esto es lo que quieres para evitar recursión.
    // Si necesitas serializar productos, considera DTOs.
    private Set<Producto> productos = new HashSet<>();

    // Opcional: Métodos helper para mantener la consistencia de la relación
    public void addSubcategoria(Categoria subcategoria) {
        this.subcategorias.add(subcategoria);
        subcategoria.setCategoriaPadre(this);
    }

    public void removeSubcategoria(Categoria subcategoria) {
        this.subcategorias.remove(subcategoria);
        subcategoria.setCategoriaPadre(null);
    }
}