package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Entities.Direccion; // Necesario para mapeo manual de direcciones
import com.ecommerce.ecommerce.Entities.Imagen;    // Necesario para mapeo manual de imagen
import com.ecommerce.ecommerce.Entities.enums.Rol;
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import com.ecommerce.ecommerce.dto.AuthResponse;
import com.ecommerce.ecommerce.dto.LoginRequest;
import com.ecommerce.ecommerce.dto.RegisterRequest;
import com.ecommerce.ecommerce.dto.UserDTO;
import com.ecommerce.ecommerce.dto.DomicilioDTO; // Para mapear direcciones
import com.ecommerce.ecommerce.dto.ImagenDTO;    // Para mapear imagen
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors; // Para el stream de mapeo

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        var user = Usuario.builder()
                .nombre(request.getFirstname())
                .apellido(request.getLastname())
                .email(request.getEmail())
                .dni(request.getDni())
                .sexo(request.getSexo())
                .password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getEmail())
                .rol(Rol.CLIENTE)
                .build();

        usuarioRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        // Mapeo manual de Usuario a UserDTO
        UserDTO userDto = mapUsuarioToUserDTO(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación"));

        var jwtToken = jwtService.generateToken(user);

        // Mapeo manual de Usuario a UserDTO
        UserDTO userDto = mapUsuarioToUserDTO(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }

    // --- Métodos de Mapeo Manual ---

    private UserDTO mapUsuarioToUserDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UserDTO userDTO = UserDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .firstname(usuario.getNombre())
                .lastname(usuario.getApellido())
                .email(usuario.getEmail())
                .dni(usuario.getDni())
                .sexo(usuario.getSexo())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .telefono(usuario.getTelefono())
                .role(usuario.getRol())
                .build();

        if (usuario.getImagenUser() != null) {
            userDTO.setProfileImage(mapImagenToImagenDTO(usuario.getImagenUser()));
        }

        if (usuario.getDirecciones() != null) {
            userDTO.setAddresses(usuario.getDirecciones().stream()
                    .map(this::mapDireccionToDomicilioDTO)
                    .collect(Collectors.toList()));
        }

        return userDTO;
    }

    private DomicilioDTO mapDireccionToDomicilioDTO(Direccion direccion) {
        if (direccion == null) {
            return null;
        }

        return DomicilioDTO.builder()
                .id(direccion.getId())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .piso(direccion.getPiso())
                .departamento(direccion.getDepartamento())
                .cp(direccion.getCp())
                .localidadNombre(direccion.getLocalidad() != null ? direccion.getLocalidad().getNombre() : null)
                .provinciaNombre(direccion.getLocalidad() != null && direccion.getLocalidad().getProvincia() != null ? direccion.getLocalidad().getProvincia().getNombre() : null)
                .build();
    }

    private ImagenDTO mapImagenToImagenDTO(Imagen imagen) {
        if (imagen == null) {
            return null;
        }
        return ImagenDTO.builder()
                .id(imagen.getId())
                .url(imagen.getDenominacion())
                .build();
    }
}