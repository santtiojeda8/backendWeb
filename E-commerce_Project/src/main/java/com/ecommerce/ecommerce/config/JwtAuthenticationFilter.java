package com.ecommerce.ecommerce.config;

import com.ecommerce.ecommerce.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull; // Para indicar que request, response y filterChain no deben ser nulos
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder; // Para manejar el contexto de seguridad
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // El bean configurado en ApplicationConfig
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Para construir los detalles de autenticación
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter; // Para asegurar que el filtro se ejecuta solo una vez por petición
import org.springframework.http.HttpHeaders; // Usar HttpHeaders de Spring para el encabezado Authorization

import java.io.IOException;


@Component // Marca el filtro como un componente gestionado por Spring
@RequiredArgsConstructor // Genera un constructor con los campos 'final' para inyección
// Extiende de OncePerRequestFilter para asegurar una única ejecución por petición
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // >>> Inyecta las dependencias necesarias <<<
    private final JwtService jwtService;               // El servicio JWT que creamos en el Paso 1
    private final UserDetailsService userDetailsService; // El bean UserDetailsService de ApplicationConfig

    // >>> Implementación del método principal del filtro <<<
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,     // La petición entrante
            @NonNull HttpServletResponse response,   // La respuesta saliente
            @NonNull FilterChain filterChain         // La cadena de filtros
    ) throws ServletException, IOException {

        // 1. Obtener el encabezado de autorización (Authorization header)
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String username;

        // 2. Verificar si el encabezado existe y tiene el formato "Bearer token"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si no hay token o no tiene el formato correcto, pasar la petición al siguiente filtro
            filterChain.doFilter(request, response);
            return; // Salir del método
        }

        // 3. Extraer el token JWT (remover el prefijo "Bearer ")
        jwt = authHeader.substring(7);

        // 4. Extraer el nombre de usuario (subject) del token
        username = jwtService.extractUsername(jwt);

        // 5. Si el nombre de usuario existe Y el usuario no está ya autenticado en el contexto de seguridad...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6. Cargar los detalles del usuario (UserDetails) usando el UserDetailsService
            // Esto busca al usuario en tu base de datos (usando tu UsuarioRepository)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 7. Validar el token: verificar que no haya expirado y que pertenezca al usuario encontrado
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 8. Si el token es válido, crear un objeto de autenticación para Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // El principal (el UserDetails del usuario)
                        null,        // Las credenciales (null en autenticación basada en token)
                        userDetails.getAuthorities() // Las autoridades/roles del usuario
                );

                // 9. Establecer detalles adicionales de la autenticación (como la IP remota, etc.)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 10. Autenticar al usuario en el contexto de seguridad de Spring
                // Esto le indica a Spring Security que el usuario actual está autenticado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Pasar la petición al siguiente filtro en la cadena (o al DispatcherServlet si es el último)
        // Si el token fue válido, la petición continuará con el usuario autenticado en el contexto.
        // Si no, la petición continuará pero el usuario no estará autenticado, y los endpoints protegidos le denegarán el acceso.
        filterChain.doFilter(request, response);
    }
}