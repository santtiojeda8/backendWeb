package com.ecommerce.ecommerce.Services;
import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Repositories.DireccionRepository;
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService extends BaseService<Usuario, Long> {
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository; // Inyectar DireccionRepository

    @Autowired // Autowired es opcional si solo hay un constructor, pero explícito es claro
    public UsuarioService(UsuarioRepository usuarioRepository, DireccionRepository direccionRepository){
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository; // Asignar DireccionRepository
    }

    // Método para encontrar un Usuario por su nombre de usuario (username)
    public Optional<Usuario> findByUserName(String userName) throws Exception {
        try {
            return usuarioRepository.findByUserName(userName);
        } catch (Exception e) {
            throw new Exception("Error al buscar usuario por nombre: " + e.getMessage());
        }
    }

    // --- Métodos para gestionar Direcciones asociadas a un Usuario ---

    @Transactional
    public List<Direccion> getDireccionesByUserId(Long userId) throws Exception {
        try {
            // Busca el usuario. Si no se encuentra, lanza una excepción.
            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + userId));

            // JPA cargará automáticamente la colección de direcciones debido a la relación @OneToMany
            return usuario.getDirecciones();
            // Si usaste FetchType.LAZY en la relación, la lista se cargará aquí.
            // Si la lista puede ser muy grande, considera paginación.
        } catch (Exception e) {
            throw new Exception("Error al obtener direcciones del usuario con ID: " + userId + ". " + e.getMessage());
        }
    }

    @Transactional
    public Direccion addDireccionToUser(Long userId, Direccion direccion) throws Exception {
        try {
            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + userId));

            // Asegura la relación bidireccional
            direccion.setUsuario(usuario);

            // Guarda la dirección. JPA debería manejar la actualización en el lado del usuario debido al cascade.
            // Sin embargo, es buena práctica añadirla a la lista del usuario explícitamente también.
            usuario.getDirecciones().add(direccion);

            // Guardamos la dirección para asegurar que tenga un ID antes de guardar el usuario (si es necesario)
            direccionRepository.save(direccion);

            // Opcional: guardar el usuario explícitamente si el cascade no parece funcionar como esperas
            // usuarioRepository.save(usuario);

            return direccion; // Devuelve la dirección guardada con su ID
        } catch (Exception e) {
            throw new Exception("Error al añadir dirección al usuario con ID: " + userId + ". " + e.getMessage());
        }
    }

    @Transactional
    public Direccion updateDireccionForUser(Long userId, Long direccionId, Direccion updatedDireccion) throws Exception {
        try {
            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + userId));

            // Busca la dirección específica por su ID
            Direccion existingDireccion = direccionRepository.findById(direccionId)
                    .orElseThrow(() -> new Exception("Dirección no encontrada con ID: " + direccionId));

            // Verifica que la dirección pertenezca al usuario
            if (!existingDireccion.getUsuario().getId().equals(userId)) {
                throw new Exception("La dirección con ID " + direccionId + " no pertenece al usuario con ID " + userId);
            }

            // Actualiza los campos de la dirección existente con los de updatedDireccion
            existingDireccion.setCalle(updatedDireccion.getCalle());
            existingDireccion.setNumero(updatedDireccion.getNumero());
            existingDireccion.setCp(updatedDireccion.getCp());
            // Asegúrate de actualizar la Localidad si es necesario:
            // existingDireccion.setLocalidad(updatedDireccion.getLocalidad());

            // Guarda la dirección actualizada
            return direccionRepository.save(existingDireccion);

        } catch (Exception e) {
            throw new Exception("Error al actualizar dirección con ID: " + direccionId + " para el usuario con ID: " + userId + ". " + e.getMessage());
        }
    }


    @Transactional
    public void removeDireccionFromUser(Long userId, Long direccionId) throws Exception {
        try {
            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + userId));

            Direccion direccionToRemove = direccionRepository.findById(direccionId)
                    .orElseThrow(() -> new Exception("Dirección no encontrada con ID: " + direccionId));

            // Verifica que la dirección pertenezca al usuario
            if (!direccionToRemove.getUsuario().getId().equals(userId)) {
                throw new Exception("La dirección con ID " + direccionId + " no pertenece al usuario con ID " + userId);
            }

            // Remueve la dirección de la lista del usuario
            usuario.getDirecciones().remove(direccionToRemove);

            // Rompe la relación bidireccional en el lado de la dirección
            direccionToRemove.setUsuario(null);

            // Elimina la dirección de la base de datos.
            // orphanRemoval = true en @OneToMany en Usuario también debería eliminarla,
            // pero eliminarla explícitamente puede ser más claro.
            direccionRepository.delete(direccionToRemove);
            // O direccionRepository.deleteById(direccionId); si orphanRemoval=true está configurado

        } catch (Exception e) {
            throw new Exception("Error al eliminar dirección con ID: " + direccionId + " para el usuario con ID: " + userId + ". " + e.getMessage());
        }
    }

    // Otros métodos que necesiten ser movidos de ClienteService/AdminService...
    // Ejemplo: Método de registro de un nuevo usuario (antes en ClienteService)
    /*
    @Transactional
    public Usuario registerNewUser(Usuario newUser) throws Exception {
        // Aquí iría la lógica de validación, encriptación de contraseña, asignación de rol (CLIENTE por defecto), etc.
        try {
            // Encriptar contraseña
            // newUser.setPassword(passwordEncoder.encode(newUser.getPassword())); // Necesitarías inyectar PasswordEncoder

            // Asignar rol por defecto si es necesario
            // if (newUser.getRol() == null) {
            //     newUser.setRol(Rol.CLIENTE); // Asumiendo que Rol.CLIENTE existe
            // }

            // Guardar el nuevo usuario
            return usuarioRepository.save(newUser);
        } catch (Exception e) {
             throw new Exception("Error al registrar nuevo usuario: " + e.getMessage());
        }
    }
    */
    // Método para cambiar el rol de un usuario (antes en AdminService o similar)
     /*
     @Transactional
     public Usuario changeUserRole(Long userId, Rol newRole) throws Exception {
         try {
             Usuario usuario = usuarioRepository.findById(userId)
                     .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + userId));
             usuario.setRol(newRole);
             return usuarioRepository.save(usuario);
         } catch (Exception e) {
             throw new Exception("Error al cambiar rol del usuario con ID: " + userId + ". " + e.getMessage());
         }
     }
     */


}
