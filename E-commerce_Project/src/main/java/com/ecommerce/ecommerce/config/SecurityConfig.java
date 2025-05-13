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

import static org.springframework.http.HttpMethod.*; // Importar métodos HTTP estáticos
import static org.springframework.security.config.Customizer.withDefaults; // Importar withDefaults

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity // Asegúrate de tener esto si usas @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF con sintaxis lambda
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())

                // 2. Configuración de reglas de autorización con sintaxis lambda
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso público a los endpoints de autenticación
                        .requestMatchers("/auth/**").permitAll()

                        // Permite acceso público a las rutas de Swagger/API Docs
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
                        .requestMatchers(GET,"/productos").permitAll()
                        .requestMatchers(GET, "/productos/dto").permitAll() // GET /productos/dto (todos los DTOs)
                        .requestMatchers(GET, "/productos/dto/promociones").permitAll() // GET /productos/dto/promociones (DTOs promocionales)
                        .requestMatchers(GET, "/productos/dto/{id}").permitAll() // GET /productos/dto/{id} (DTO por ID)
                        .requestMatchers(GET, "/productos/buscar").permitAll() // GET /productos/buscar (búsqueda por nombre
                        .requestMatchers(GET, "/categorias/**").permitAll()
                        .requestMatchers(GET, "/localidades/**").permitAll()
                        .requestMatchers(GET, "/provincia/**").permitAll()



                        // Cualquier otra solicitud que no coincida con las reglas anteriores requiere autenticación
                        // Esta regla debe ir al FINAL de las reglas de autorización
                        .anyRequest().authenticated()
                )

                // 3. Configurar la política de sesión como sin estado (STATELESS)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Configurar el AuthenticationProvider personalizado (esto no cambia)
                .authenticationProvider(authenticationProvider)

                // 5. Añadir nuestro filtro JWT antes del filtro de autenticación de nombre de usuario/contraseña (esto tampoco cambia)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Construye y retorna la cadena de filtros de seguridad
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Permite cualquier origen
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Métodos permitidos
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todos los headers
        configuration.setAllowCredentials(true); // Permite credenciales (cookies, auth headers)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica esta configuración a todas las rutas
        return source;
    }

}
