package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Entities.enums.Rol; // Asegúrate de que esta importación sea correcta
import com.ecommerce.ecommerce.Services.UsuarioService;
import com.ecommerce.ecommerce.dto.AdminUserUpdateDTO;
import com.ecommerce.ecommerce.dto.UserDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/usuarios") // <--- ¡RUTA BASE para el administrador!
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen (¡ajustar para producción!)
@PreAuthorize("hasRole('ADMIN')") // <--- ¡Asegura que solo los ADMINS accedan a este controlador!
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene una lista de todos los usuarios (activos e inactivos).
     * Requiere rol de ADMINISTRADOR.
     * GET /api/v1/admin/usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsersForAdmin() {
        try {
            // Llama al método findAll() heredado de BaseService que obtiene todos los usuarios
            List<Usuario> usuarios = usuarioService.findAll();
            List<UserDTO> userDTOS = usuarios.stream()
                    .map(usuarioService::mapToUserDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(userDTOS);
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios para el administrador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtiene los detalles de un usuario específico por ID (incluyendo inactivos).
     * Requiere rol de ADMINISTRADOR.
     * GET /api/v1/admin/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserByIdForAdmin(@PathVariable Long id) {
        try {
            // Llama al método buscarPorIdIncluyendoInactivos() heredado de BaseService
            Usuario usuario = usuarioService.buscarPorIdIncluyendoInactivos(id);
            return ResponseEntity.status(HttpStatus.OK).body(usuarioService.mapToUserDTO(usuario));
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por ID para el administrador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Actualiza el estado 'activo' y el 'rol' de un usuario.
     * Requiere rol de ADMINISTRADOR.
     * PUT /api/v1/admin/usuarios/{id}/update-status-role
     */
    @PutMapping("/{id}/update-status-role")
    public ResponseEntity<UserDTO> updateStatusAndRole(@PathVariable Long id, @RequestBody AdminUserUpdateDTO updateDTO) {
        try {
            // Busca al usuario incluyendo inactivos para que el admin pueda modificar su estado
            Usuario usuarioExistente = usuarioService.buscarPorIdIncluyendoInactivos(id);

            if (updateDTO.getActivo() != null) {
                usuarioExistente.setActivo(updateDTO.getActivo());
            }

            if (updateDTO.getRol() != null) {
                usuarioExistente.setRol(updateDTO.getRol());
            }

            // Guarda los cambios usando el método 'actualizar' del BaseService
            Usuario usuarioActualizado = usuarioService.actualizar(usuarioExistente);
            return ResponseEntity.status(HttpStatus.OK).body(usuarioService.mapToUserDTO(usuarioActualizado));
        } catch (Exception e) {
            System.err.println("Error al actualizar estado/rol del usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Desactiva lógicamente una cuenta de usuario (soft delete).
     * Requiere rol de ADMINISTRADOR.
     * PUT /api/v1/admin/usuarios/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            usuarioService.deactivateAccount(id); // Usa el método específico del UsuarioService
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"message\":\"Usuario desactivado exitosamente.\"}");
        } catch (Exception e) {
            System.err.println("Error al desactivar el usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Error al desactivar el usuario: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Activa una cuenta de usuario previamente desactivada.
     * Requiere rol de ADMINISTRADOR.
     * PUT /api/v1/admin/usuarios/{id}/activate
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        try {
            Usuario usuarioActivado = usuarioService.activar(id); // Usa el método 'activar' del BaseService
            return ResponseEntity.status(HttpStatus.OK).body(usuarioService.mapToUserDTO(usuarioActivado));
        } catch (Exception e) {
            System.err.println("Error al activar el usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Elimina un usuario físicamente de la base de datos (¡Hard Delete!).
     * Requiere rol de ADMINISTRADOR.
     * ¡ÚSALO CON EXTREMA PRECAUCIÓN! Los datos se perderán irreversiblemente.
     * DELETE /api/v1/admin/usuarios/hard-delete/{id}
     */
    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<?> hardDeleteUser(@PathVariable Long id) {
        try {
            usuarioService.hardDelete(id); // Usa el método 'hardDelete' del BaseService
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"message\":\"Usuario eliminado físicamente exitosamente.\"}");
        } catch (Exception e) {
            System.err.println("Error al eliminar físicamente el usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Error al eliminar físicamente el usuario: " + e.getMessage() + "\"}");
        }
    }
}