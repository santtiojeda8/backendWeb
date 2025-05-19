package com.ecommerce.ecommerce.Entities;
import com.ecommerce.ecommerce.Entities.enums.Rol;
import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
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
// Eliminamos las anotaciones de herencia ya que Usuario ya no será clase base de otras entidades que hereden directamente para Cliente/Admin
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// @DiscriminatorColumn(name = "tipo_usuario")
@EqualsAndHashCode(callSuper = true)
// >>> IMPLEMENTAR USERDETAILS <<<
public class Usuario extends Base implements UserDetails { // Usuario ahora será la entidad principal de usuario

    @Column(name = "auth_id")
    private String auth0Id;

    @Column(name = "user_name", unique = true) // <<-- AÑADIDO: Asegura unicidad en la DB
    private String userName; // Este campo lo usaremos como el 'username' para Spring Security

    private String nombre;
    private String apellido;

    @Column(name = "email", unique = true) // <<-- AÑADIDO: Asegura unicidad en la DB
    protected String email;

    private Integer dni;

    // >>> CAMPO SEXO USANDO TU ENUM <<<
    @Enumerated(EnumType.STRING) // Indica a JPA que guarde el enum como un String en la DB
    @Column(name = "sexo", length = 20) // <<< --- AÑADIMOS ESTA LINEA (y nombre de columna opcional) y length --- >>>
    private Sexo sexo; // Campo para almacenar el sexo usando tu Enum


    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol; // Tu enum Rol (ADMIN, CLIENTE, etc.) ahora define el tipo de usuario

    @OneToOne // Assuming OneToOne relationship with Imagen
    @JoinColumn(name="imagen_id")
    protected Imagen imagenUser; // Asumiendo que Imagen es otra entidad


    @Column(name = "password")
    @JsonIgnore // Para no serializar la contraseña en respuestas JSON
    private String password;  // Almacena la contraseña encriptada


    // --- AÑADIMOS LA RELACIÓN CON DIRECCIONES ---
    // Esta relación estaba en Cliente, ahora la movemos a Usuario
    // Usamos OneToMany en Usuario y ManyToOne en Direccion (una dirección pertenece a un Usuario)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // 'usuario' es el nombre del campo en Direccion
    @Builder.Default
    @JsonIgnore // Para evitar bucles infinitos en serialización si Direccion tiene una referencia a Usuario
    private List<Direccion> direcciones = new ArrayList<>();


    // >>> IMPLEMENTACIÓN DE MÉTODOS DE USERDETAILS (no cambian) <<<

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Las autoridades ahora se basan directamente en el Rol del Usuario
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

    // --- Métodos de conveniencia (opcional) ---
    public void addDireccion(Direccion direccion) {
        direcciones.add(direccion);
        direccion.setUsuario(this); // Asegura la relación bidireccional
    }

    public void removeDireccion(Direccion direccion) {
        direcciones.remove(direccion);
        direccion.setUsuario(null); // Rompe la relación bidireccional
    }
    // --- Fin Métodos de conveniencia ---
}