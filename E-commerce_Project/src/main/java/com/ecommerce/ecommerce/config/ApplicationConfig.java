package com.ecommerce.ecommerce.config;

import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Indica a Spring que esta clase contiene configuraciones (@Bean methods)
@RequiredArgsConstructor // Genera un constructor con los campos 'final' para inyección
public class ApplicationConfig {

    // Inyecta tu Repositorio de Usuario. Lombok lo inyectará vía constructor.
    private final UsuarioRepository usuarioRepository;

    // >>> Bean UserDetailsService <<<
    // Define cómo Spring Security carga los detalles de un usuario por su nombre de usuario.
    @Bean
    public UserDetailsService userDetailsService() {
        // Retorna una implementación de UserDetailsService usando una expresión lambda.
        // Ahora, busca el usuario en tu repositorio por el 'username' o 'email'
        // pero SOLO si la cuenta está ACTIVA.
        return usernameOrEmail -> {
            // Intenta buscar por username y que esté activo
            return usuarioRepository.findByUserNameAndActivoTrue(usernameOrEmail)
                    .or(() -> usuarioRepository.findByEmailAndActivoTrue(usernameOrEmail)) // Si no lo encuentra por username, intenta por email (activo)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo: " + usernameOrEmail));
        };
    }

    // >>> Bean AuthenticationProvider <<<
    // Define la fuente de los detalles del usuario (UserDetailsService)
    // y cómo se verifica la contraseña (PasswordEncoder).
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Asigna el UserDetailsService que definimos arriba
        authProvider.setPasswordEncoder(passwordEncoder);       // Asigna el PasswordEncoder que definiremos abajo
        return authProvider;
    }

    // >>> Bean AuthenticationManager <<<
    // Este es el componente principal de Spring Security que maneja el proceso de autenticación.
    // Lo necesitamos en el AuthService para realizar la autenticación durante el login.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // Spring Boot configura automáticamente uno que usa el AuthenticationProvider definido.
    }

    // >>> Bean PasswordEncoder <<<
    // Define cómo se encriptan y verifican las contraseñas.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}