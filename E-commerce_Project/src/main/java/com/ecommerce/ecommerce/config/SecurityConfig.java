package com.ecommerce.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*; // Importa todos los métodos HTTP estáticamente
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity // Permite usar @PreAuthorize en métodos de controladores
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF, común para APIs RESTful con JWT
                .cors(withDefaults()) // Habilita CORS usando el bean corsConfigurationSource

                .authorizeHttpRequests(auth -> auth
                        // Permitir explícitamente todas las solicitudes OPTIONS (crucial para CORS preflight)
                        .requestMatchers(OPTIONS, "/**").permitAll()

                        // 1. Rutas de Autenticación y Registro (Públicas)
                        .requestMatchers(POST, "/auth/register").permitAll()
                        .requestMatchers(POST, "/auth/login").permitAll()

                        // 2. Rutas del Perfil de Usuario Autenticado (Requieren Token JWT)
                        // Para obtener los datos del propio usuario (GET /auth/me)
                        .requestMatchers(GET, "/auth/me").authenticated()
                        // Para subir la imagen de perfil (POST /auth/profile/upload-image)
                        .requestMatchers(POST, "/auth/profile/upload-image").authenticated()
                        // Para actualizar los datos del perfil (PUT /auth/profile)
                        .requestMatchers(PUT, "/auth/profile").authenticated()
                        // Para actualizar credenciales (PATCH /auth/update-credentials)
                        .requestMatchers(PATCH, "/auth/update-credentials").authenticated()
                        // Para desactivar la cuenta (DELETE /auth/deactivate)
                        .requestMatchers(DELETE, "/auth/deactivate").authenticated()

                        // 3. Rutas de Swagger/API Docs (Públicas)
                        .requestMatchers(
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html")
                        .permitAll()

                        // 4. Rutas de Archivos de Subida (Públicas, para acceder a las imágenes)
                        .requestMatchers("/uploads/**").permitAll()

                        // 5. Rutas de Productos (Públicas)
                        .requestMatchers(GET,"/productos").permitAll()
                        .requestMatchers(GET,"/productos/categorias").permitAll()
                        .requestMatchers(GET,"/productos/colores").permitAll()
                        .requestMatchers(GET,"/productos/talles").permitAll()
                        .requestMatchers(GET,"/productos/filtrar").permitAll()
                        .requestMatchers(POST,"/productos/filtrar").permitAll() // Si /productos/filtrar es POST público
                        .requestMatchers(GET, "/productos/dto").permitAll()
                        .requestMatchers(GET, "/productos/dto/promociones").permitAll()
                        .requestMatchers(GET, "/productos/dto/{id}").permitAll()
                        .requestMatchers(GET, "/productos/buscar").permitAll()

                        // 6. Rutas de Categorías, Localidades, Provincias (Públicas)
                        .requestMatchers(GET, "/categorias/**").permitAll()
                        .requestMatchers(GET, "/localidades/**").permitAll()
                        .requestMatchers(GET, "/provincias/**").permitAll()

                        // 7. Rutas de ProductoDetalle (Públicas)
                        .requestMatchers(GET, "/producto_detalle/buscar").permitAll()
                        .requestMatchers(GET, "/producto_detalle/producto/{productoId}").permitAll()
                        .requestMatchers(GET, "/producto_detalle/stock-mayor-a/{stockMinimo}").permitAll()
                        .requestMatchers(GET, "/producto_detalle/filtrar").permitAll()
                        .requestMatchers(GET, "/producto_detalle/talles/{productoId}").permitAll()
                        .requestMatchers(GET, "/producto_detalle/colores/{productoId}").permitAll()
                        .requestMatchers(GET, "/producto_detalle/disponible").permitAll()

                        // 8. Cualquier otra solicitud REQUIERE autenticación.
                        // Esta debe ser la ÚLTIMA regla, ya que es la más general.
                        .anyRequest().authenticated()
                )
                // Configuración de la gestión de sesiones como Stateless (sin estado, esencial para JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Asigna el AuthenticationProvider personalizado (que usa tu UserDetailsService y PasswordEncoder)
                .authenticationProvider(authenticationProvider)
                // Añade el filtro JWT antes del filtro de autenticación de usuario/contraseña estándar de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para la configuración de CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite orígenes específicos (ej. tu frontend). Asegúrate de que sea el puerto correcto.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // Métodos HTTP permitidos para las solicitudes CORS
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Encabezados permitidos en las solicitudes (importante para Content-Type y Authorization)
        configuration.setAllowedHeaders(Arrays.asList("*")); // '*' es permisivo; considera ser más específico en producción
        // Permite el envío de credenciales (ej. cookies, headers de autorización)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración CORS a todas las rutas (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}