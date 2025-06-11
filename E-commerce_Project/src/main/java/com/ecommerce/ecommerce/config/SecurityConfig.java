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
                        .requestMatchers(OPTIONS, "/**").permitAll()

                        // 1. Rutas de Autenticación y Registro (Públicas)
                        .requestMatchers(POST, "/auth/register").permitAll()
                        .requestMatchers(POST, "/auth/login").permitAll()

                        // Rutas de Mercado Pago (Públicas)
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers(POST, "/api/mercadopago/webhook").permitAll() // Si lo tienes

                        // ⭐⭐⭐ CAMBIO AQUÍ: Permitir acceso a la ruta de órdenes por ID existente ⭐⭐⭐
                        // Esta es la ruta correcta para obtener la orden por ID en tu backend actual
                        .requestMatchers(GET, "/orden_compra/dto/{id}").permitAll() // <-- NUEVA LÍNEA CLAVE

                        // 2. Rutas del Perfil de Usuario Autenticado (Requieren Token JWT)
                        .requestMatchers(GET, "/auth/me").authenticated()
                        .requestMatchers(POST, "/auth/profile/upload-image").authenticated()
                        .requestMatchers(PUT, "/auth/profile").authenticated()
                        .requestMatchers(PATCH, "/auth/update-credentials").authenticated()
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

                        // 5. Rutas de Productos, Categorías, Localidades, etc. (Públicas)
                        .requestMatchers(GET,
                                "/productos/**",
                                "/colores/**",
                                "/talles/**",
                                "/categorias/**",
                                "/localidades/**",
                                "/provincias/**",
                                "/producto_detalle/**"
                        ).permitAll()

                        // Si estos POST /productos/filtrar son públicos
                        .requestMatchers(POST, "/productos/filtrar").permitAll()

                        // 7. Cualquier otra solicitud REQUIERE autenticación.
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}