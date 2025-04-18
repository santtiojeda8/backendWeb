/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zapatillas.ecommerce.model;

/**
 *
 * @author astud
 */
import javax.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String email;

    private String contraseña;

    @Enumerated(EnumType.STRING)
    private Rol rol;  // Enum: ADMIN / CLIENTE

    private String dni;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioDireccion> usuarioDirecciones;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrdenDeCompra> ordenesDeCompra;

    // Constructor sin parámetros (para JPA)
    public Usuario() {
    }

    // Constructor con parámetros para crear el objeto
    public Usuario(String nombre, String email, String contraseña, Rol rol, String dni) {
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
        this.rol = rol;
        this.dni = dni;
    }

    // Métodos getters y setters (generados por Lombok @Data)
    // Si no usas Lombok, puedes generarlos manualmente.
}