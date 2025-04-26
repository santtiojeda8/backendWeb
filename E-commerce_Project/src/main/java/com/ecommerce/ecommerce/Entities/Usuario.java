package com.ecommerce.ecommerce.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // Especifica que todas las subclases se guardarán en una sola tabla
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)  // Columna discriminadora
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Usuario extends Base {

    @Column(name = "auth_id")
    private String auth0Id;

    @Column(name = "user_name")
    private String userName;

    private String nombre;

    private String apellido;

    protected String email;

    protected Number dni;

    @OneToOne
    @JoinColumn(name="imagen_id")
    protected Imagen imagenUser;

    @Column(name = "password")
    private String password;  // Almacena la contraseña encriptada
}
