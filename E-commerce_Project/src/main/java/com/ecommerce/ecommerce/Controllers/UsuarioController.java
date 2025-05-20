package com.ecommerce.ecommerce.Controllers;
import com.ecommerce.ecommerce.Entities.Direccion; // Importar Direccion
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importar HttpStatus
import org.springframework.http.ResponseEntity; // Importar ResponseEntity
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends BaseController<Usuario,Long>{
    // Eliminamos el @Autowired aquí si ya lo inyectamos en el constructor
    // @Autowired
    private final UsuarioService usuarioService; // Usamos final y lo inyectamos por constructor

    @Autowired // Inyección por constructor (recomendado)
    public UsuarioController(UsuarioService usuarioService){
        super(usuarioService);
        this.usuarioService = usuarioService; // Asignamos el servicio
    }

    // El BaseController ya te da endpoints básicos como GET /usuarios, GET /usuarios/{id}, POST /usuarios, PUT /usuarios/{id}, DELETE /usuarios/{id}

    // Puedes añadir endpoints específicos de usuario aquí si los necesitas, por ejemplo, buscar por username
    @GetMapping("/by-username/{username}")
    public ResponseEntity<Usuario> getUsuarioByUsername(@PathVariable String username) {
        try {
            // Usamos el Optional<Usuario> devuelto por el servicio
            Usuario usuario = usuarioService.findByUserName(username)
                    .orElseThrow(() -> new Exception("Usuario no encontrado con username: " + username));
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            // Manejo de errores: usuario no encontrado u otro error
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // O un mensaje de error
        }
    }

    // --- Endpoints para gestionar Direcciones de un Usuario ---

    // GET /usuarios/{userId}/direcciones - Obtener todas las direcciones de un usuario
    @GetMapping("/{userId}/direcciones")
    public ResponseEntity<List<Direccion>> getDireccionesByUserId(@PathVariable Long userId) {
        try {
            List<Direccion> direcciones = usuarioService.getDireccionesByUserId(userId);
            return ResponseEntity.ok(direcciones);
        } catch (Exception e) {
            // Manejo de errores: usuario no encontrado u otro error
            // CORRECCIÓN: Se corrigió HttpStatus.NOT_NOT_FOUND a HttpStatus.NOT_FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // O un mensaje de error
        }
    }

    // POST /usuarios/{userId}/direcciones - Añadir una nueva dirección a un usuario
    @PostMapping("/{userId}/direcciones")
    public ResponseEntity<Direccion> addDireccionToUser(@PathVariable Long userId, @RequestBody Direccion direccion) {
        try {
            Direccion savedDireccion = usuarioService.addDireccionToUser(userId, direccion);
            // Devolvemos 201 Created si la creación fue exitosa
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDireccion);
        } catch (Exception e) {
            // Manejo de errores: usuario no encontrado, validación fallida, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un mensaje de error
        }
    }

    // PUT /usuarios/{userId}/direcciones/{direccionId} - Actualizar una dirección específica de un usuario
    @PutMapping("/{userId}/direcciones/{direccionId}")
    public ResponseEntity<Direccion> updateDireccionForUser(@PathVariable Long userId, @PathVariable Long direccionId, @RequestBody Direccion updatedDireccion) {
        try {
            Direccion savedDireccion = usuarioService.updateDireccionForUser(userId, direccionId, updatedDireccion);
            return ResponseEntity.ok(savedDireccion);
        } catch (Exception e) {
            // Manejo de errores: usuario/dirección no encontrada, validación fallida, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un mensaje de error
        }
    }


    // DELETE /usuarios/{userId}/direcciones/{direccionId} - Eliminar una dirección específica de un usuario
    @DeleteMapping("/{userId}/direcciones/{direccionId}")
    public ResponseEntity<?> removeDireccionFromUser(@PathVariable Long userId, @PathVariable Long direccionId) {
        try {
            usuarioService.removeDireccionFromUser(userId, direccionId);
            // Devolvemos 204 No Content para indicar que la eliminación fue exitosa y no hay contenido que devolver
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            // Manejo de errores: usuario/dirección no encontrada, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un mensaje de error
        }
    }


    // Otros endpoints que necesiten ser movidos de ClienteController/AdminController...
    // Ejemplo: Endpoint para registrar un nuevo usuario (antes en ClienteController)
    /*
    @PostMapping("/register")
    public ResponseEntity<Usuario> registerNewUser(@RequestBody Usuario newUser) {
        try {
            Usuario registeredUser = usuarioService.registerNewUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un mensaje de error
        }
    }
    */
    // Endpoint para cambiar el rol de un usuario (requiere permisos de ADMIN, usar @PreAuthorize)
     /*
     @PreAuthorize("hasRole('ADMIN')") // Ejemplo de cómo proteger el endpoint
     @PutMapping("/{userId}/role")
     public ResponseEntity<Usuario> changeUserRole(@PathVariable Long userId, @RequestBody Rol newRole) {
         try {
             Usuario updatedUser = usuarioService.changeUserRole(userId, newRole);
             return ResponseEntity.ok(updatedUser);
         } catch (Exception e) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un mensaje de error
         }
     }
     */

}