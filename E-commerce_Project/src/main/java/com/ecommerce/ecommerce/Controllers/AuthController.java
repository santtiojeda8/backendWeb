package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Services.AuthService;
import com.ecommerce.ecommerce.Services.UsuarioService;
import com.ecommerce.ecommerce.dto.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ecommerce.ecommerce.Entities.Usuario; // Importa tu entidad Usuario aquí
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Nueva importación para la opción más limpia

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() throws Exception {
        UserDTO currentUser = usuarioService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    // *** MÉTODO updateProfile CORREGIDO ***
    @PutMapping("/profile")
    // Usar @AuthenticationPrincipal es la forma más limpia y recomendada
    public ResponseEntity<UserDTO> updateProfile(
            @RequestBody UserProfileUpdateDTO userProfileUpdateDTO,
            @AuthenticationPrincipal Usuario usuarioAutenticado // Spring Security inyecta tu entidad Usuario
    ) throws Exception {
        if (usuarioAutenticado == null) {
            throw new IllegalStateException("No se pudo obtener el usuario autenticado.");
        }

        // Obtener el ID directamente de la entidad Usuario inyectada
        Long userId = usuarioAutenticado.getId();

        // Puedes añadir logs temporales para verificar si lo obtienes
        System.out.println("DEBUG: ID del usuario autenticado: " + userId);
        System.out.println("DEBUG: Username del usuario autenticado (email): " + usuarioAutenticado.getUsername());

        UserDTO updatedUser = usuarioService.updateProfile(userId, userProfileUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // *** MÉTODO uploadProfileImage CORREGIDO ***
    @PostMapping("/profile/upload-image")
    public ResponseEntity<UserDTO> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Usuario usuarioAutenticado // Inyecta tu entidad Usuario
    ) throws Exception {
        if (usuarioAutenticado == null) {
            throw new IllegalStateException("No se pudo obtener el usuario autenticado para subir la imagen.");
        }

        Long userId = usuarioAutenticado.getId();
        System.out.println("DEBUG: ID del usuario autenticado para imagen: " + userId);

        UserDTO updatedUser = usuarioService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(updatedUser);
    }

    // *** MÉTODO updateUserCredentials CORREGIDO ***
    @PatchMapping("/update-credentials")
    public ResponseEntity<UserDTO> updateUserCredentials(
            @RequestBody UpdateCredentialsRequest request,
            @AuthenticationPrincipal Usuario usuarioAutenticado // Inyecta tu entidad Usuario
    ) throws Exception {
        if (usuarioAutenticado == null) {
            throw new IllegalStateException("No se pudo obtener el usuario autenticado para actualizar credenciales.");
        }

        Long userId = usuarioAutenticado.getId();
        System.out.println("DEBUG: ID del usuario autenticado para credenciales: " + userId);

        UserDTO updatedUser = usuarioService.updateCredentials(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
}