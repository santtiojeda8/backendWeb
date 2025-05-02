package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Rol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
public class Usuario extends Base{
    @Column(name = "auth_id")
    private String auth0Id;
    @Column(name = "user_name")
    private String userName;
    private String nombre;
    private String apellido;
    protected String email;
    private Integer dni;
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Rol rol;

    @OneToOne
    @JoinColumn(name="imagen_id")
    protected Imagen imagenUser;

    @Column(name = "password")
    @JsonIgnore
    private String password;  // Almacena la contraseña encriptada


}
