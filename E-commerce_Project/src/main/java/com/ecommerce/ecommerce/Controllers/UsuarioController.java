package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Services.UsuarioService;
import com.ecommerce.ecommerce.Services.BaseService; // Necesario para BaseController
import com.ecommerce.ecommerce.dto.UserDTO;
import com.ecommerce.ecommerce.dto.UserProfileUpdateDTO;
import com.ecommerce.ecommerce.dto.UpdateCredentialsRequest; // Importar si lo usas

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

// ¡¡¡FALTAN ESTAS IMPORTACIONES PARA SPRING SECURITY!!!
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
// FIN DE LAS IMPORTACIONES FALTANTES

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional; // Necesario para Optional

@RestController
@RequestMapping("/usuarios")

public class UsuarioController extends BaseController<Usuario,Long>{
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService){
        // PROBLEMA 1: Aquí está el error. Si BaseController espera un BaseService<Usuario, Long>,
        // y UsuarioService implementa/extiende BaseService<Usuario, Long>, esto debería funcionar.
        // Si no funciona, significa que UsuarioService no implementa BaseService<Usuario, Long>.
        // Asumiendo que UsuarioService SÍ implementa o extiende BaseService<Usuario, Long>:
        super((BaseService<Usuario, Long>) usuarioService); // <-- Casteo explícito, o revisar herencia/interfaces
        this.usuarioService = usuarioService;
    }

    // Endpoint de ejemplo para obtener usuario por username
    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserDTO> getUsuarioByUsername(@PathVariable String username) {
        try {
            // PROBLEMA 2: 'findUserDTOByUsername' no existe todavía en UsuarioService.
            // Vamos a añadirlo o, si solo tienes findByUserName, mapearlo aquí.
            // Opción 1: Si quieres que el servicio devuelva DTO:
            // UserDTO userDTO = usuarioService.findUserDTOByUsername(username);
            // Opción 2: Si el servicio solo devuelve la Entidad, mapea aquí:
            Optional<Usuario> usuarioOptional = usuarioService.findByUserName(username); // Suponiendo que tienes este en tu UsuarioRepository o servicio
            if (usuarioOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(usuarioService.mapToUserDTO(usuarioOptional.get())); // mapToUserDTO DEBE ser público en UsuarioService.
            // Si no lo es, muévelo a un "Mapper" o recrea el DTO aquí.
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por username: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Cambiado a INTERNAL_SERVER_ERROR
        }
    }

    // --- Endpoints para Perfil de Usuario (Autenticado) ---

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
            Long userId = getUserIdFromAuthentication(); // Método auxiliar (se arreglará más abajo)
            UserDTO updatedUser = usuarioService.updateProfile(userId, userProfileUpdateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.err.println("Error al actualizar perfil de usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/profile/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = getUserIdFromAuthentication(); // Método auxiliar (se arreglará más abajo)
            UserDTO updatedUser = usuarioService.uploadProfileImage(userId, file);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.err.println("Error al subir imagen de perfil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/credentials/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateCredentials(@RequestBody UpdateCredentialsRequest request) {
        try {
            Long userId = getUserIdFromAuthentication(); // Método auxiliar (se arreglará más abajo)
            UserDTO updatedUser = usuarioService.updateCredentials(userId, request);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.err.println("Error al actualizar credenciales: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoints para gestionar Direcciones de un Usuario ---
    // PROBLEMA 2: Estos métodos de gestión de direcciones no existen en UsuarioService.
    // Los marcaremos como "a implementar" en el servicio.

    @GetMapping("/{userId}/direcciones")
    public ResponseEntity<List<Direccion>> getDireccionesByUserId(@PathVariable Long userId) {
        try {
            List<Direccion> direcciones = usuarioService.getDireccionesByUserId(userId); // Requiere implementación en UsuarioService
            return ResponseEntity.ok(direcciones);
        } catch (Exception e) {
            System.err.println("Error al obtener direcciones para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Cambiado a INTERNAL_SERVER_ERROR
        }
    }

    @PostMapping("/{userId}/direcciones")
    public ResponseEntity<Direccion> addDireccionToUser(@PathVariable Long userId, @RequestBody Direccion direccion) {
        try {
            Direccion savedDireccion = usuarioService.addDireccionToUser(userId, direccion); // Requiere implementación en UsuarioService
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDireccion);
        } catch (Exception e) {
            System.err.println("Error al añadir dirección para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{userId}/direcciones/{direccionId}")
    public ResponseEntity<Direccion> updateDireccionForUser(@PathVariable Long userId, @PathVariable Long direccionId, @RequestBody Direccion updatedDireccion) {
        try {
            Direccion savedDireccion = usuarioService.updateDireccionForUser(userId, direccionId, updatedDireccion); // Requiere implementación en UsuarioService
            return ResponseEntity.ok(savedDireccion);
        } catch (Exception e) {
            System.err.println("Error al actualizar dirección " + direccionId + " para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{userId}/direcciones/{direccionId}")
    public ResponseEntity<?> removeDireccionFromUser(@PathVariable Long userId, @PathVariable Long direccionId) {
        try {
            usuarioService.removeDireccionFromUser(userId, direccionId); // Requiere implementación en UsuarioService
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            System.err.println("Error al eliminar dirección " + direccionId + " para el usuario " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // --- Método auxiliar para obtener el ID del usuario autenticado ---
    private Long getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            System.err.println("ERROR: No hay autenticación en el SecurityContext.");
            throw new IllegalStateException("No se pudo obtener el ID del usuario autenticado: No hay autenticación.");
        }

        if (!authentication.isAuthenticated()) {
            System.err.println("ERROR: El usuario no está autenticado.");
            throw new IllegalStateException("No se pudo obtener el ID del usuario autenticado: El usuario no está autenticado.");
        }

        Object principal = authentication.getPrincipal();

        System.out.println("DEBUG: Tipo del principal: " + principal.getClass().getName()); // Para depuración

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // Si tu UserDetails personalizado tiene un getId(), úsalo directamente:
            // return userDetails.getId();
            String usernameOrEmail = userDetails.getUsername();
            System.out.println("DEBUG: Principal es UserDetails, username: " + usernameOrEmail);
            try {
                return Long.parseLong(usernameOrEmail); // Intentar parsear como ID
            } catch (NumberFormatException e) {
                // Si no es un ID, intenta buscar por email
                // **IMPORTANTE:** Asumo que tienes un método en tu servicio para esto.
                // Si no lo tienes, debes crearlo (o usar el que ya tenías).
                // **Asegúrate de que este método maneje excepciones correctamente.**
                // return usuarioService.getUserIdByUsernameOrEmail(usernameOrEmail);
                System.err.println("ERROR: El username no es un ID numérico. Necesitas un método para obtener el ID por username/email.");
                throw new IllegalStateException("No se pudo obtener el ID del usuario autenticado: El username no es un ID numérico.");
            }
        } else if (principal instanceof Long) {
            System.out.println("DEBUG: Principal es Long: " + principal);
            return (Long) principal;
        } else if (principal instanceof String) {
            String usernameOrEmail = (String) principal;
            System.out.println("DEBUG: Principal es String: " + usernameOrEmail);
            try {
                return Long.parseLong(usernameOrEmail); // Intentar parsear como ID
            } catch (NumberFormatException e) {
                // Si no es un ID, intenta buscar por email
                // **IMPORTANTE:** Asumo que tienes un método en tu servicio para esto.
                // Si no lo tienes, debes crearlo (o usar el que ya tenías).
                // **Asegúrate de que este método maneje excepciones correctamente.**
                // return usuarioService.getUserIdByUsernameOrEmail(usernameOrEmail);
                System.err.println("ERROR: El String principal no es un ID numérico. Necesitas un método para obtener el ID por username/email.");
                throw new IllegalStateException("No se pudo obtener el ID del usuario autenticado: El String principal no es un ID numérico.");
            }
        } else {
            System.err.println("ERROR: Formato de principal de seguridad no soportado: " + principal.getClass().getName());
            throw new IllegalStateException("Formato de principal de seguridad no soportado. No se pudo obtener el ID del usuario.");
        }
    }
}