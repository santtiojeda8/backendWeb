package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Services.AuthService;
import com.ecommerce.ecommerce.dto.AuthResponse;
import com.ecommerce.ecommerce.dto.LoginRequest;
import com.ecommerce.ecommerce.dto.RegisterRequest;
import lombok.AllArgsConstructor; // Usamos @AllArgsConstructor para inyección vía constructor
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    // Inyecta el AuthService que creamos en el Paso 3. Lombok se encarga con @AllArgsConstructor.
    private final AuthService authService;

    // >>> Endpoint para Registro <<<
    // Mapea las peticiones POST a /auth/register
    @PostMapping("/register")
    // Recibe el cuerpo de la petición como un objeto RegisterRequest
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        // Llama al método register del AuthService y devuelve la respuesta con el token.
        return ResponseEntity.ok(authService.register(request));
    }

    // >>> Endpoint para Inicio de Sesión <<<
    // Mapea las peticiones POST a /auth/login
    @PostMapping("/login")
    // Recibe el cuerpo de la petición como un objeto LoginRequest
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        // Llama al método login del AuthService y devuelve la respuesta con el token.
        return ResponseEntity.ok(authService.login(request));
    }
}