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
import com.ecommerce.ecommerce.Entities.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus; // Importar HttpStatus

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

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @RequestBody UserProfileUpdateDTO userProfileUpdateDTO,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) throws Exception {
        if (usuarioAutenticado == null) {
            throw new IllegalStateException("No se pudo obtener el usuario autenticado.");
        }
        Long userId = usuarioAutenticado.getId();
        System.out.println("DEBUG: ID del usuario autenticado: " + userId);
        System.out.println("DEBUG: Username del usuario autenticado (email): " + usuarioAutenticado.getUsername());
        UserDTO updatedUser = usuarioService.updateProfile(userId, userProfileUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/profile/upload-image")
    public ResponseEntity<UserDTO> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) throws Exception {
        if (usuarioAutenticado == null) {
            throw new IllegalStateException("No se pudo obtener el usuario autenticado para subir la imagen.");
        }
        Long userId = usuarioAutenticado.getId();
        System.out.println("DEBUG: ID del usuario autenticado para imagen: " + userId);
        UserDTO updatedUser = usuarioService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/update-credentials")
    public ResponseEntity<UserDTO> updateUserCredentials(
            @RequestBody UpdateCredentialsRequest request,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) throws Exception {
        if (usuarioAutenticado == null) {
            throw new IllegalStateException("No se pudo obtener el usuario autenticado para actualizar credenciales.");
        }
        Long userId = usuarioAutenticado.getId();
        System.out.println("DEBUG: ID del usuario autenticado para credenciales: " + userId);
        UserDTO updatedUser = usuarioService.updateCredentials(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    // *** NUEVO ENDPOINT PARA DESACTIVAR LA CUENTA EN AUTHCONTROLLER ***
    @DeleteMapping("/deactivate")
    // @PreAuthorize("isAuthenticated()") // Ya está manejado por la configuración general de SecurityConfig si /auth/** es authenticated
    public ResponseEntity<?> deactivateCurrentUserAccount(
            @AuthenticationPrincipal Usuario usuarioAutenticado // Inyecta tu entidad Usuario
    ) {
        if (usuarioAutenticado == null) {
            // Esto debería ser capturado por Spring Security antes de llegar aquí si no está autenticado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        try {
            Long userId = usuarioAutenticado.getId(); // Obtiene el ID del usuario autenticado directamente
            usuarioService.deactivateAccount(userId);
            return ResponseEntity.ok("Cuenta desactivada exitosamente. La sesión ha sido invalidada.");
            // O, más apropiado para un DELETE sin contenido de respuesta:
            // return ResponseEntity.noContent().build(); // Devuelve un 204 No Content
        } catch (RuntimeException e) {
            System.err.println("Error al desactivar la cuenta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al desactivar la cuenta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al desactivar la cuenta.");
        }
    }

}