/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zapatillas.ecommerce.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_direccion")
@Data  // Lombok generará automáticamente los getters, setters, toString, etc.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDireccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Usuario
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Relación con Direccion
    @ManyToOne
    @JoinColumn(name = "id_direccion", nullable = false)
    private Direccion direccion;

    // Constructor sin argumentos (usado por JPA)
    public UsuarioDireccion() {}

    // Constructor con los atributos
    public UsuarioDireccion(Usuario usuario, Direccion direccion) {
        this.usuario = usuario;
        this.direccion = direccion;
    }
}