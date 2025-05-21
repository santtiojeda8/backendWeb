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

import java.time.LocalDate; // <-- NUEVO: Importar LocalDate
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Usuario extends Base implements UserDetails {

    @Column(name = "auth_id")
    private String auth0Id;

    @Column(name = "user_name", unique = true)
    private String userName;

    private String nombre;
    private String apellido;

    @Column(name = "email", unique = true)
    protected String email;

    private Integer dni;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", length = 20)
    private Sexo sexo;

    // --- NUEVOS CAMPOS ---
    @Column(name = "fecha_nacimiento") // <-- NUEVO CAMPO
    private LocalDate fechaNacimiento;

    @Column(name = "telefono") // <-- NUEVO CAMPO
    private String telefono;
    // --- FIN NUEVOS CAMPOS ---

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) // Añadir cascade y orphanRemoval para la imagen
    @JoinColumn(name="imagen_id")
    protected Imagen imagenUser;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Direccion> direcciones = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Asegúrate de que el nombre del rol tenga el prefijo "ROLE_"
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public void addDireccion(Direccion direccion) {
        direcciones.add(direccion);
        direccion.setUsuario(this);
    }

    public void removeDireccion(Direccion direccion) {
        direcciones.remove(direccion);
        direccion.setUsuario(null);
    }
}