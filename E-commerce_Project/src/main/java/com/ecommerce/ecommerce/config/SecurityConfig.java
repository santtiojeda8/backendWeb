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

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // Permitir explícitamente todas las solicitudes OPTIONS
                        .requestMatchers(OPTIONS, "/**").permitAll()

                        // 1. **MÁS ESPECÍFICO Y PRIMERO**: Autenticación y Registro (POST)
                        .requestMatchers(POST, "/auth/register").permitAll()
                        .requestMatchers(POST, "/auth/login").permitAll()

                        // Permite PUT en /auth/profile (necesita autenticación)
                        .requestMatchers(PUT, "/auth/profile").authenticated()

                        // Para el GET del propio perfil (si lo usas)
                        .requestMatchers(GET, "/auth/profile").authenticated()

                        // 2. Rutas de Swagger/API Docs (GET, etc.)
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

                        // Rutas de archivos de subida
                        .requestMatchers("/uploads/**").permitAll()

                        // 3. Rutas de productos (GET, etc.) que no requieren autenticación
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
                        .requestMatchers(GET, "/categorias/**").permitAll()
                        .requestMatchers(GET, "/localidades/**").permitAll()
                        .requestMatchers(GET, "/provincias/**").permitAll()

                        // *********************************************************************************
                        // ¡¡¡CAMBIO CLAVE AQUÍ!!!
                        // 4. Rutas de ProductoDetalle (GET) que no requieren autenticación
                        // Añade estas líneas para permitir el acceso público a los endpoints de ProductoDetalle
                        .requestMatchers(GET, "/producto_detalle/buscar").permitAll()
                        .requestMatchers(GET, "/producto_detalle/producto/{productoId}").permitAll()
                        .requestMatchers(GET, "/producto_detalle/stock-mayor-a/{stockMinimo}").permitAll()
                        .requestMatchers(GET, "/producto_detalle/filtrar").permitAll()
                        .requestMatchers(GET, "/producto_detalle/talles/{productoId}").permitAll()
                        .requestMatchers(GET, "/producto_detalle/colores/{productoId}").permitAll()
                        .requestMatchers(GET, "/producto_detalle/disponible").permitAll()
                        // *********************************************************************************

                        // 5. Cualquier otra solicitud REQUIERE autenticación
                        .requestMatchers(PATCH, "/auth/update-credentials").authenticated()
                        .anyRequest().authenticated() // Esta debe ser la última regla

                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS Configuration (parece correcta, pero la revisamos para asegurar)
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Asegúrate de que este origen sea EXACTAMENTE el de tu frontend
        // Si usas Vite o similar, 5173 es común. Si es CRA, suele ser 3000.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // Lista explícita de métodos. OPTIONS es crucial para preflight requests de CORS
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Permite todos los headers, incluyendo Content-Type y Authorization. Esto es importante.
        // Para desarrollo, '*' está bien. Para producción, ser más específico es mejor (e.g., "Content-Type", "Authorization", "Accept").
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Permite credenciales (cookies, headers de autenticación). Necesario para enviar el JWT si lo usas en otras peticiones.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a todas las rutas
        return source;
    }

}