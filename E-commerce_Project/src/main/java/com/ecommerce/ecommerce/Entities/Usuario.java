package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Rol;
import jakarta.persistence.*;


@Entity
@Table(name = "Usuarios")

public class Usuario extends Base {

    @Column(name = "nombre")
    private String name ;
    @Column(name = "constraseña")
    private String password;
    @Column(name = "usuario")
    private Rol user;
    @Column(name = "email")
    private String email;
    @Column(name = "dni")
    private String dni;

}
