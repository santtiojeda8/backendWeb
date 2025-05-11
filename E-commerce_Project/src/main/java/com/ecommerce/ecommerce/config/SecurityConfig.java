package com.ecommerce.ecommerce.config;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuración de reglas de autorización con sintaxis lambda
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**",
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
                        .anyRequest()
                        .authenticated()
                )

                // 2. Deshabilitar CSRF con sintaxis lambda
                // En lugar de .csrf().disable(), usamos .csrf(csrf -> csrf.disable())
                .csrf(csrf -> csrf.disable())

                // 3. Configurar la política de sesión con sintaxis lambda
                // En lugar de .sessionManagement().sessionCreationPolicy(...), usamos .sessionManagement(session -> session.sessionCreationPolicy(...))
                // También se elimina el .and() que ya no es necesario con este estilo
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Configurar el AuthenticationProvider personalizado (esto no cambia)
                .authenticationProvider(authenticationProvider)

                // 5. Añadir nuestro filtro JWT (esto tampoco cambia)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
