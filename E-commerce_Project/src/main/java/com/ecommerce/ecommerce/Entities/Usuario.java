package com.ecommerce.ecommerce.Entities;



import com.ecommerce.ecommerce.Entities.enums.Rol;
import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
// >>> Imports de Spring Security <<<
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "usuarios")
@Data // Provee getters, setters, toString, equals, hashCode
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
@EqualsAndHashCode(callSuper = true)
// >>> IMPLEMENTAR USERDETAILS <<<
public class Usuario extends Base implements UserDetails {
    @Column(name = "auth_id")
    private String auth0Id;

    @Column(name = "user_name")
    private String userName; // Este campo lo usaremos como el 'username' para Spring Security

    private String nombre;
    private String apellido;

    protected String email;

    private Integer dni;

    // >>> AÑADIR CAMPO SEXO USANDO TU ENUM <<<
    @Enumerated(EnumType.STRING) // Indica a JPA que guarde el enum como un String en la DB
    @Column(name = "sexo", length = 20) // <<< --- AÑADE ESTA LINEA (y nombre de columna opcional) y length --- >>>
    private Sexo sexo; // Campo para almacenar el sexo usando tu Enum


    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Rol rol; // Tu enum Rol

    @OneToOne
    @JoinColumn(name="imagen_id")
    protected Imagen imagenUser; // Asumiendo que Imagen es otra entidad

    @Column(name = "password")
    @JsonIgnore
    private String password;  // Almacena la contraseña encriptada

    // >>> IMPLEMENTACIÓN DE MÉTODOS DE USERDETAILS (no cambian) <<<

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName; // Mapeado a tu campo userName
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
    // ... otros métodos si tienes ...
}