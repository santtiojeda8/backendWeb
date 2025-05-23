package com.ecommerce.ecommerce.Controllers;

// Importaciones de DTOs y Servicios
import com.ecommerce.ecommerce.Entities.Direccion; // Todavía necesaria para getDireccionesByUserId si el servicio devuelve entidades
import com.ecommerce.ecommerce.Services.UsuarioService;
import com.ecommerce.ecommerce.Services.BaseService;
import com.ecommerce.ecommerce.dto.UserDTO;
import com.ecommerce.ecommerce.dto.UserProfileUpdateDTO;
import com.ecommerce.ecommerce.dto.UpdateCredentialsRequest;
import com.ecommerce.ecommerce.dto.DomicilioDTO; // <-- Importación para DomicilioDTO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

// Importaciones para Spring Security
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails; // No se necesita directamente aquí ya que se delega al servicio


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <-- ¡NUEVA IMPORTACIÓN!

import com.ecommerce.ecommerce.Entities.Usuario; // Sólo si BaseController lo requiere directamente

@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends BaseController<Usuario,Long>{

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService){
        super((BaseService<Usuario, Long>) usuarioService);
        this.usuarioService = usuarioService;
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserDTO> getUsuarioByUsername(@PathVariable String username) {
        try {
            Optional<Usuario> usuarioOptional = usuarioService.findByUserName(username);
            if (usuarioOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(usuarioService.mapToUserDTO(usuarioOptional.get()));
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por username: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        try {
            UserDTO userDTO = usuarioService.getCurrentUser();
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            System.err.println("Error al obtener perfil de usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/profile/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateCurrentUserProfile(@RequestBody UserProfileUpdateDTO userProfileUpdateDTO) {
        try {
            Long userId = usuarioService.getUserIdByUsernameOrEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            UserDTO updatedUser = usuarioService.updateProfile(userId, userProfileUpdateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            System.err.println("Error al actualizar perfil de usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Error inesperado al actualizar perfil de usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/profile/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = usuarioService.getUserIdByUsernameOrEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            UserDTO updatedUser = usuarioService.uploadProfileImage(userId, file);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            System.err.println("Error al subir imagen de perfil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Error inesperado al subir imagen de perfil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/credentials/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateCredentials(@RequestBody UpdateCredentialsRequest request) {
        try {
            Long userId = usuarioService.getUserIdByUsernameOrEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            UserDTO updatedUser = usuarioService.updateCredentials(userId, request);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            System.err.println("Error al actualizar credenciales: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Error inesperado al actualizar credenciales: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoints para gestionar Direcciones de un Usuario (Usando DomicilioDTO) ---

    @GetMapping("/{userId}/direcciones")
    public ResponseEntity<List<DomicilioDTO>> getDireccionesByUserId(@PathVariable Long userId) {
        try {
            // mapToDomicilioDTO ahora es public en UsuarioService
            List<Direccion> direcciones = usuarioService.getDireccionesByUserId(userId);
            List<DomicilioDTO> domicilioDTOs = direcciones.stream()
                    .map(usuarioService::mapToDomicilioDTO) // Usa method reference
                    .collect(Collectors.toList());
            return ResponseEntity.ok(domicilioDTOs);
        } catch (RuntimeException e) {
            System.err.println("Error al obtener direcciones para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Error inesperado al obtener direcciones para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{userId}/direcciones")
    public ResponseEntity<DomicilioDTO> addDireccionToUser(@PathVariable Long userId, @RequestBody DomicilioDTO domicilioDTO) {
        try {
            // El servicio ahora espera DomicilioDTO y devuelve Direccion (que mapeamos a DTO)
            Direccion newDireccion = usuarioService.addDireccionToUser(userId, domicilioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.mapToDomicilioDTO(newDireccion));
        } catch (RuntimeException e) {
            System.err.println("Error al añadir dirección para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Error inesperado al añadir dirección para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{userId}/direcciones/{direccionId}")
    public ResponseEntity<DomicilioDTO> updateDireccionForUser(@PathVariable Long userId, @PathVariable Long direccionId, @RequestBody DomicilioDTO updatedDomicilioDTO) {
        try {
            // El servicio ahora espera DomicilioDTO y devuelve Direccion (que mapeamos a DTO)
            Direccion savedDireccion = usuarioService.updateDireccionForUser(userId, direccionId, updatedDomicilioDTO);
            return ResponseEntity.ok(usuarioService.mapToDomicilioDTO(savedDireccion));
        } catch (RuntimeException e) {
            System.err.println("Error al actualizar dirección " + direccionId + " para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Error inesperado al actualizar dirección " + direccionId + " para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{userId}/direcciones/{direccionId}")
    public ResponseEntity<?> removeDireccionFromUser(@PathVariable Long userId, @PathVariable Long direccionId) {
        try {
            // El servicio ahora espera DomicilioDTO y devuelve Direccion (que mapeamos a DTO)
            usuarioService.removeDireccionFromUser(userId, direccionId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            System.err.println("Error al eliminar dirección " + direccionId + " para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Error inesperado al eliminar dirección " + direccionId + " para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // El método getUserIdFromAuthentication ahora es privado y auxiliar.
    // La lógica principal se ha movido directamente a los métodos del controlador.
    private Long getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado.");
        }
        // Delega la lógica de obtener el ID a tu UsuarioService, que ya la tiene
        return usuarioService.getUserIdByUsernameOrEmail(authentication.getName());
    }
}