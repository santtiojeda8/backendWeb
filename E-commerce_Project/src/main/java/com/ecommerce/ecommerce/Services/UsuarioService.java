package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Entities.Provincia;
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Entities.Imagen; // Asegúrate de que la entidad Imagen sea la correcta
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import com.ecommerce.ecommerce.Repositories.DireccionRepository;
import com.ecommerce.ecommerce.dto.DomicilioDTO;
import com.ecommerce.ecommerce.dto.ImagenDTO; // Asegúrate de que ImagenDTO sea la correcta
import com.ecommerce.ecommerce.dto.UpdateCredentialsRequest;
import com.ecommerce.ecommerce.dto.UserDTO; // Asegúrate de que UserDTO sea la correcta
import com.ecommerce.ecommerce.dto.UserProfileUpdateDTO;

import org.springframework.beans.factory.annotation.Value; // Importar para @Value
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException; // Para manejar excepciones de archivo
import java.nio.file.Files; // Para operaciones de archivo
import java.nio.file.Path; // Para rutas de archivo
import java.nio.file.Paths; // Para construir rutas
import java.nio.file.StandardCopyOption; // Para opciones de copia de archivo
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID; // Para generar nombres de archivo únicos
import java.util.stream.Collectors;

@Service
public class UsuarioService extends BaseService<Usuario,Long> {

    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;

    @Value("${file.upload-dir}") // Inyecta el valor de la propiedad del directorio de subidas
    private String uploadDir;

    public UsuarioService(UsuarioRepository usuarioRepository, DireccionRepository direccionRepository){
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
    }

    @Transactional
    public UserDTO updateProfile(Long userId, UserProfileUpdateDTO updateDTO) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(userId);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        Usuario usuario = optionalUsuario.get();

        // Lógica de actualización de campos no nulos
        if (updateDTO.getFirstname() != null) {
            usuario.setNombre(updateDTO.getFirstname());
        }
        if (updateDTO.getLastname() != null) {
            usuario.setApellido(updateDTO.getLastname());
        }
        usuario.setDni(updateDTO.getDni());
        usuario.setSexo(updateDTO.getSexo());
        usuario.setFechaNacimiento(updateDTO.getFechaNacimiento());

        if (updateDTO.getTelefono() != null) {
            usuario.setTelefono(updateDTO.getTelefono());
        }

        // Lógica de actualización de direcciones (como la tienes, asumiendo cascade.ALL y orphanRemoval=true)
        if (updateDTO.getAddresses() != null) {
            Set<Direccion> direccionesActualizadas = new HashSet<>();
            for (DomicilioDTO dtoAddress : updateDTO.getAddresses()) {
                Direccion existingAddress = usuario.getDirecciones().stream()
                        .filter(a -> a.getId() != null && a.getId().equals(dtoAddress.getId()))
                        .findFirst()
                        .orElse(null);
                if (existingAddress != null) {
                    existingAddress.setCalle(dtoAddress.getCalle());
                    existingAddress.setNumero(dtoAddress.getNumero());
                    existingAddress.setPiso(dtoAddress.getPiso());
                    existingAddress.setDepartamento(dtoAddress.getDepartamento());
                    existingAddress.setCp(dtoAddress.getCp());
                    // Asume que Localidad y Provincia se manejan aparte si es necesario actualizar solo sus nombres
                    direccionesActualizadas.add(existingAddress);
                } else {
                    Direccion newAddress = new Direccion();
                    newAddress.setCalle(dtoAddress.getCalle());
                    newAddress.setNumero(dtoAddress.getNumero());
                    newAddress.setPiso(dtoAddress.getPiso());
                    newAddress.setDepartamento(dtoAddress.getDepartamento());
                    newAddress.setCp(dtoAddress.getCp());
                    newAddress.setUsuario(usuario); // Asegúrate de vincular la nueva dirección al usuario
                    // Si tienes Localidad y Provincia DTOs, necesitarías buscar/crear las entidades aquí
                    direccionesActualizadas.add(newAddress);
                }
            }
            // Elimina las referencias existentes y añade las actualizadas
            // Asegúrate de que la relación @OneToMany en Usuario tenga orphanRemoval=true para que las direcciones eliminadas se borren de la BD
            usuario.getDirecciones().clear();
            usuario.getDirecciones().addAll(direccionesActualizadas);
        } else if (updateDTO.getAddresses() != null && updateDTO.getAddresses().isEmpty()) {
            // Si la lista de direcciones se envía vacía, se eliminan todas las direcciones del usuario
            usuario.getDirecciones().forEach(address -> address.setUsuario(null)); // Desvincula para orphanRemoval
            usuario.getDirecciones().clear();
        }

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return mapToUserDTO(savedUsuario);
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado.");
        }
        Long userId = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            try {
                // Si el username es el ID del usuario
                userId = Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                // Si el username es el email del usuario
                Usuario userByEmail = usuarioRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("Usuario con email " + userDetails.getUsername() + " no encontrado."));
                userId = userByEmail.getId();
            }
        } else if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            throw new IllegalStateException("Formato de principal de seguridad no soportado. No se pudo obtener el ID del usuario.");
        }
        if (userId == null) {
            throw new IllegalStateException("ID de usuario no encontrado en la autenticación.");
        }
        final Long finalUserId = userId;
        Usuario usuario = usuarioRepository.findById(finalUserId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + finalUserId + " no encontrado en la base de datos."));
        return mapToUserDTO(usuario);
    }

    public UserDTO mapToUserDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        List<DomicilioDTO> domicilioDTOs = null;
        if (usuario.getDirecciones() != null) {
            domicilioDTOs = usuario.getDirecciones().stream()
                    .map(this::mapToDomicilioDTO)
                    .collect(Collectors.toList());
        }
        ImagenDTO imagenDTO = null;
        // Asegúrate de que el nombre del campo de la imagen en Usuario sea `imagenUser`
        if (usuario.getImagenUser() != null) {
            imagenDTO = mapToImagenDTO(usuario.getImagenUser());
        }
        return UserDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .firstname(usuario.getNombre())
                .lastname(usuario.getApellido())
                .email(usuario.getEmail())
                .dni(usuario.getDni())
                .sexo(usuario.getSexo())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .telefono(usuario.getTelefono())
                .addresses(domicilioDTOs)
                .role(usuario.getRol())
                .profileImage(imagenDTO)
                .build();
    }

    private DomicilioDTO mapToDomicilioDTO(Direccion direccion) {
        if (direccion == null) {
            return null;
        }
        return DomicilioDTO.builder()
                .id(direccion.getId())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .piso(direccion.getPiso())
                .departamento(direccion.getDepartamento())
                .cp(direccion.getCp())
                .localidadNombre(direccion.getLocalidad() != null ? direccion.getLocalidad().getNombre() : null)
                .provinciaNombre(direccion.getLocalidad() != null && direccion.getLocalidad().getProvincia() != null ?
                        direccion.getLocalidad().getProvincia().getNombre() : null)
                .build();
    }

    private ImagenDTO mapToImagenDTO(Imagen imagen) {
        if (imagen == null) {
            return null;
        }
        return ImagenDTO.builder()
                .id(imagen.getId())
                .url(imagen.getDenominacion()) // 'denominacion' de la entidad Imagen es la URL pública
                .build();
    }

    @Transactional
    public UserDTO uploadProfileImage(Long userId, MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío.");
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(userId);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        Usuario usuario = optionalUsuario.get();

        // Obtener la extensión del archivo original
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generar un nombre de archivo único usando UUID
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path copyLocation = Paths.get(uploadDir + java.io.File.separator + uniqueFilename);

        // Asegurarse de que el directorio de subida exista
        try {
            Files.createDirectories(copyLocation.getParent());
        } catch (IOException e) {
            throw new IOException("No se pudo crear el directorio de subida: " + copyLocation.getParent(), e);
        }

        // Guardar el archivo físicamente
        try {
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Error al guardar el archivo de imagen: " + uniqueFilename, e);
        }

        // Construir la URL completa que será accesible desde el frontend
        // Usa la URL de tu aplicación, por ejemplo, "http://localhost:8080" en desarrollo.
        // En producción, sería "https://tudominio.com"
        String baseUrl = "http://localhost:8080"; // <<< ¡CAMBIA ESTO PARA PRODUCCIÓN!
        String imageUrl = baseUrl + "/uploads/" + uniqueFilename; // La URL accesible públicamente

        Imagen imagenUser = usuario.getImagenUser();
        if (imagenUser == null) {
            imagenUser = new Imagen();
        }
        imagenUser.setDenominacion(imageUrl); // Guarda la URL completa en la base de datos
        // Si tu entidad Imagen tiene un campo para la ruta local del archivo (recomendado):
        // imagenUser.setRutaLocal(copyLocation.toString()); // Descomenta y añade este campo a tu entidad Imagen

        usuario.setImagenUser(imagenUser); // Asocia la imagen al usuario
        // El `usuarioRepository.save()` persistirá la entidad `Imagen` debido a la relación `CascadeType.ALL`
        Usuario savedUsuario = usuarioRepository.save(usuario);

        return mapToUserDTO(savedUsuario);
    }

    @Transactional
    public UserDTO updateCredentials(Long userId, UpdateCredentialsRequest request) throws Exception {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(userId);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        Usuario usuario = optionalUsuario.get();
        if (request.getNewEmail() != null && !request.getNewEmail().isEmpty()) {
            usuario.setEmail(request.getNewEmail());
        }
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            // TODO: Implementar lógica de cambio de contraseña con PasswordEncoder
            // usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        usuarioRepository.save(usuario);
        return mapToUserDTO(usuario);
    }

    // --- MÉTODOS REQUERIDOS POR EL CONTROLADOR ---

    @Transactional(readOnly = true)
    public Optional<Usuario> findByUserName(String username) {
        return usuarioRepository.findByUserName(username);
    }

    @Transactional(readOnly = true)
    public Long getUserIdByUsernameOrEmail(String usernameOrEmail) {
        try {
            return Long.parseLong(usernameOrEmail);
        } catch (NumberFormatException e) {
            Usuario usuario = usuarioRepository.findByEmail(usernameOrEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario con username/email " + usernameOrEmail + " no encontrado."));
            return usuario.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<Direccion> getDireccionesByUserId(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        return List.copyOf(usuario.getDirecciones());
    }

    @Transactional
    public Direccion addDireccionToUser(Long userId, Direccion direccion) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        direccion.setUsuario(usuario);
        usuario.getDirecciones().add(direccion);
        usuarioRepository.save(usuario);
        return direccion;
    }

    @Transactional
    public Direccion updateDireccionForUser(Long userId, Long direccionId, Direccion updatedDireccion) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        Direccion existingDireccion = usuario.getDirecciones().stream()
                .filter(d -> d.getId().equals(direccionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + direccionId + " para el usuario " + userId));

        existingDireccion.setCalle(updatedDireccion.getCalle());
        existingDireccion.setNumero(updatedDireccion.getNumero());
        existingDireccion.setPiso(updatedDireccion.getPiso());
        existingDireccion.setDepartamento(updatedDireccion.getDepartamento());
        existingDireccion.setCp(updatedDireccion.getCp());
        // TODO: Actualizar Localidad y Provincia si es necesario.

        usuarioRepository.save(usuario);
        return existingDireccion;
    }

    @Transactional
    public void removeDireccionFromUser(Long userId, Long direccionId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        boolean removed = usuario.getDirecciones().removeIf(d -> d.getId().equals(direccionId));

        if (!removed) {
            throw new RuntimeException("Dirección no encontrada con ID: " + direccionId + " para el usuario " + userId);
        }
        usuarioRepository.save(usuario);
    }
}