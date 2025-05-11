package com.ecommerce.ecommerce.Services;


// >>> Imports de Spring Security y Lombok <<<
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Entities.enums.Rol;
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import com.ecommerce.ecommerce.dto.AuthResponse;
import com.ecommerce.ecommerce.dto.LoginRequest;
import com.ecommerce.ecommerce.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService; // Verifica este import
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // >>> Método de Registro (ACTUALIZADO) <<<
    public AuthResponse register(RegisterRequest request) {
        // 1. Construir un objeto Usuario a partir de los datos del RegisterRequest
        var user = Usuario.builder()
                .nombre(request.getFirstname()) // Mapea firstname del request a nombre en entidad
                .apellido(request.getLastname()) // Mapea lastname del request a apellido en entidad

                // >>> Mapear email del request a email en entidad <<<
                .email(request.getEmail())

                // >>> Mapear email del request a userName en entidad (para login) <<<
                .userName(request.getEmail()) // Usamos el email como nombre de usuario para Spring Security

                // >>> Mapear sexo del request a sexo en entidad <<<
                .sexo(request.getSexo()) // Asegúrate de que el getter getSexo() exista en RegisterRequest

                .password(passwordEncoder.encode(request.getPassword())) // Encriptar la contraseña
                .rol(Rol.CLIENTE) // Asignar rol CLIENTE por defecto (ajusta si necesitas lógica)
                // Si Admin tiene campos adicionales (como 'activo'), NO puedes crearlo directamente aquí con builder de Usuario.
                // Si el registro fuera solo para Clientes/Usuarios base, esto estaría bien.
                // Si necesitas crear admins, necesitarías lógica separada o un builder específico para Admin.
                .build();

        // 4. Guardar el nuevo usuario en la base de datos (Spring JPA guardará el subtipo correcto si es necesario, aunque con SINGLE_TABLE el tipo está en el discriminador)
        usuarioRepository.save(user); // Guarda la entidad Usuario (que puede ser Cliente o Admin si el discriminador se manejara aquí)

        // 5. Generar el token JWT
        var jwtToken = jwtService.getToken(user);

        // 6. Devolver la respuesta con el token
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    // >>> Método de Inicio de Sesión (ACTUALIZADO) <<<
    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar usando email y contraseña
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), // >>> USAR request.getEmail() para autenticar <<<
                        request.getPassword()
                )
        );

        // 2. Buscar el usuario por el email (que ahora es el userName para login)
        var user = usuarioRepository.findByUserName(request.getEmail()) // >>> USAR findByUserName con request.getEmail() <<<
                .orElseThrow(); // Si autentica, debe existir.

        // 3. Generar el token JWT
        var jwtToken = jwtService.getToken(user);

        // 4. Devolver la respuesta
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}