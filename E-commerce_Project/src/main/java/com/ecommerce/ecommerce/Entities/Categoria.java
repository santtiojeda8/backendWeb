package com.ecommerce.ecommerce.Entities;

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
    @OneToMany(mappedBy = "categoriaPadre")
    @Builder.Default
    private Set<Categoria> subcategorias=new HashSet<>();
    @ManyToOne
    @JoinColumn(name="categoriaPadreID")
    private Categoria categoriaPadre;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

}
