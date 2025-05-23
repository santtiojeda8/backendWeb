package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Entities.Imagen;
import com.ecommerce.ecommerce.Entities.enums.Rol;
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import com.ecommerce.ecommerce.dto.AuthResponse;
import com.ecommerce.ecommerce.dto.LoginRequest;
import com.ecommerce.ecommerce.dto.RegisterRequest;
import com.ecommerce.ecommerce.dto.UserDTO;
import com.ecommerce.ecommerce.dto.DomicilioDTO;
import com.ecommerce.ecommerce.dto.ImagenDTO;
import com.ecommerce.ecommerce.dto.LocalidadDTO;
import com.ecommerce.ecommerce.dto.ProvinciaDTO;
// Importa el DTO de la solicitud de actualización de credenciales
import com.ecommerce.ecommerce.dto.UpdateCredentialsRequest; // <-- ¡NUEVA IMPORTACIÓN!
// Importa tu excepción personalizada
import com.ecommerce.ecommerce.exception.InvalidCurrentPasswordException; // <-- ¡NUEVA IMPORTACIÓN!

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Para si el usuario no existe
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService; // Asumo que tienes un JwtService para generar tokens
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // ... (Tu código existente de register)
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

        UserDTO userDto = mapUsuarioToUserDTO(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // ... (Tu código existente de login)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación"));

        var jwtToken = jwtService.generateToken(user);

        UserDTO userDto = mapUsuarioToUserDTO(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }

    // --- ¡NUEVO MÉTODO PARA ACTUALIZAR CREDENCIALES! ---
    @Transactional
    public void updateCredentials(String userEmail, UpdateCredentialsRequest request) {
        // userEmail viene del token JWT autenticado (obtenido en el controlador)
        Usuario user = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + userEmail));

        // 1. Validar la contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            // Lanza la excepción personalizada si la contraseña actual es incorrecta
            throw new InvalidCurrentPasswordException("La contraseña actual es incorrecta.");
        }

        // 2. Procede con las actualizaciones si la contraseña actual es correcta
        boolean changed = false;

        if (request.getNewEmail() != null && !request.getNewEmail().trim().isEmpty()) {
            if (!request.getNewEmail().trim().equals(user.getEmail())) { // Solo actualiza si es diferente
                // Opcional: Verifica si el nuevo email ya está en uso por otro usuario
                if (usuarioRepository.findByEmail(request.getNewEmail().trim()).isPresent() &&
                        !usuarioRepository.findByEmail(request.getNewEmail().trim()).get().getId().equals(user.getId())) {
                    throw new IllegalArgumentException("El nuevo correo electrónico ya está en uso por otro usuario.");
                }
                user.setEmail(request.getNewEmail().trim());
                changed = true;
            }
        }

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            // Opcional: Asegúrate de que la nueva contraseña no sea igual a la actual
            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la actual.");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword())); // <-- ¡IMPORTANTE: Codificar la nueva contraseña!
            changed = true;
        }

        if (changed) {
            usuarioRepository.save(user); // Guarda los cambios si se realizó alguno
        } else {
            // Opcional: Podrías lanzar una excepción si no hay cambios,
            // pero el frontend ya tiene una validación para esto.
            // throw new IllegalArgumentException("No hay cambios en el email o la contraseña para guardar.");
        }
    }
    // --- FIN DEL NUEVO MÉTODO ---


    // --- Métodos de Mapeo Manual (tu código existente) ---

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

        LocalidadDTO localidadDTO = null;
        if (direccion.getLocalidad() != null) {
            ProvinciaDTO provinciaDTO = null;
            if (direccion.getLocalidad().getProvincia() != null) {
                provinciaDTO = ProvinciaDTO.builder()
                        .id(direccion.getLocalidad().getProvincia().getId())
                        .nombre(direccion.getLocalidad().getProvincia().getNombre())
                        .build();
            }
            localidadDTO = LocalidadDTO.builder()
                    .id(direccion.getLocalidad().getId())
                    .nombre(direccion.getLocalidad().getNombre())
                    .provincia(provinciaDTO)
                    .build();
        }

        return DomicilioDTO.builder()
                .id(direccion.getId())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .piso(direccion.getPiso())
                .departamento(direccion.getDepartamento())
                .cp(direccion.getCp())
                .localidad(localidadDTO)
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