package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Usuario; // Tu entidad Usuario
import com.ecommerce.ecommerce.Entities.enums.Rol; // Tu enum Rol (ADMIN, CLIENTE, etc.)

import com.ecommerce.ecommerce.Repositories.UsuarioRepository; // <-- Correcto, usas UsuarioRepository
import com.ecommerce.ecommerce.dto.AuthResponse; // DTO de respuesta de autenticación
import com.ecommerce.ecommerce.dto.LoginRequest; // DTO de solicitud de login
import com.ecommerce.ecommerce.dto.RegisterRequest; // DTO de solicitud de registro
import lombok.RequiredArgsConstructor; // Para la inyección de dependencias mediante constructor
import org.springframework.security.authentication.AuthenticationManager; // Gestor de autenticación de Spring Security
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Token de autenticación para usuario/contraseña
import org.springframework.security.crypto.password.PasswordEncoder; // Codificador de contraseñas
import org.springframework.stereotype.Service; // Marca esta clase como un servicio de Spring

@Service // Anotación para que Spring gestione este bean como un servicio
@RequiredArgsConstructor // Genera un constructor con los campos 'final' para inyección de dependencias
public class AuthService {

    // Inyección de dependencias a través del constructor generado por Lombok
    private final UsuarioRepository usuarioRepository; // <-- Asegúrate que el nombre del repositorio sea 'usuarioRepository'
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario en el sistema.
     * Asigna el rol por defecto 'CLIENTE' al usuario.
     *
     * @param request Objeto que contiene los datos de registro del usuario.
     * @return AuthResponse que contiene el token JWT generado para el nuevo usuario.
     */
    public AuthResponse register(RegisterRequest request) {
        // Construye la entidad Usuario a partir de los datos del RegisterRequest.
        // Se utiliza el @SuperBuilder de Lombok para construir la entidad.
        var user = Usuario.builder()
                // >>> CAMBIO 1: Usar getFirstname() y getLastname() de RegisterRequest <<<
                .nombre(request.getFirstname())   // <-- Cambiado de getNombre() a getFirstname()
                .apellido(request.getLastname())  // <-- Cambiado de getApellido() a getLastname()
                .email(request.getEmail())
                .dni(request.getDni())            // <-- Esto ya lo tenías correcto
                .sexo(request.getSexo())          // El enum Sexo se mapea correctamente

                // Cifrado de la contraseña: MUY IMPORTANTE NUNCA GUARDAR CONTRASEÑAS EN TEXTO PLANO
                .password(passwordEncoder.encode(request.getPassword()))

                // Asignar el email como userName para Spring Security, ya que es el identificador único.
                // Tu entidad Usuario usa 'userName' para la implementación de UserDetails.getUsername().
                .userName(request.getEmail())

                // >>> ASIGNACIÓN DEL ROL POR DEFECTO: CLIENTE <<<
                // Este es el punto clave para resolver el error 'tipo_usuario' doesn't have a default value
                .rol(Rol.CLIENTE)
                .build();

        // Guarda el nuevo usuario en la base de datos a través del repositorio.
        usuarioRepository.save(user);

        // >>> CAMBIO 2: Asegurarse de que generateToken sea PUBLIC en JwtService <<<
        var jwtToken = jwtService.generateToken(user); // Este método debe ser público en JwtService

        // Construye y devuelve la respuesta de autenticación con el token.
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Autentica a un usuario existente y devuelve un token JWT.
     *
     * @param request Objeto que contiene las credenciales de login (email y contraseña).
     * @return AuthResponse que contiene el token JWT si la autenticación es exitosa.
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son inválidas.
     */
    public AuthResponse login(LoginRequest request) {
        // Autentica al usuario usando el AuthenticationManager.
        // Esto verifica las credenciales y, si son válidas, establece el contexto de seguridad.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), // Usa el email como el "username" para la autenticación
                        request.getPassword()
                )
        );

        // Si la autenticación es exitosa, busca al usuario en la base de datos por su email.
        // .orElseThrow() lanzará una RuntimeException si el usuario no es encontrado (aunque Spring Security
        // ya habría manejado esto si las credenciales fueran incorrectas).
        var user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación"));

        // >>> CAMBIO 3: Asegurarse de que generateToken sea PUBLIC en JwtService <<<
        var jwtToken = jwtService.generateToken(user); // Este método debe ser público en JwtService

        // Construye y devuelve la respuesta de autenticación con el token.
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}