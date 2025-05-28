package com.ecommerce.ecommerce.Services;

// ... (todas tus importaciones existentes)

import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Entities.Provincia;
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Entities.Imagen;
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import com.ecommerce.ecommerce.Repositories.DireccionRepository;
import com.ecommerce.ecommerce.Repositories.LocalidadRepository;
import com.ecommerce.ecommerce.dto.DomicilioDTO;
import com.ecommerce.ecommerce.dto.ImagenDTO;
import com.ecommerce.ecommerce.dto.LocalidadDTO;
import com.ecommerce.ecommerce.dto.ProvinciaDTO;
import com.ecommerce.ecommerce.dto.UpdateCredentialsRequest;
import com.ecommerce.ecommerce.dto.UserDTO;
import com.ecommerce.ecommerce.dto.UserProfileUpdateDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
// Nuevas importaciones necesarias para UserDetailsService
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections; // Para SimpleGrantedAuthority


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UsuarioService extends BaseService<Usuario,Long> implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final LocalidadRepository localidadRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          DireccionRepository direccionRepository,
                          LocalidadRepository localidadRepository,
                          PasswordEncoder passwordEncoder){
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
        this.localidadRepository = localidadRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // *** MÉTODO loadUserByUsername para la autenticación de Spring Security ***
    @Override
    @Transactional(readOnly = true) // Es una operación de lectura
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Usuario usuario;

        // Primero intenta buscar por userName
        Optional<Usuario> byUserName = usuarioRepository.findByUserNameAndActivoTrue(usernameOrEmail);

        if (byUserName.isPresent()) {
            usuario = byUserName.get();
        } else {
            // Si no se encuentra por userName, intenta buscar por email
            Optional<Usuario> byEmail = usuarioRepository.findByEmailAndActivoTrue(usernameOrEmail);
            if (byEmail.isPresent()) {
                usuario = byEmail.get();
            } else {
                // Si no se encuentra por ninguno de los dos, el usuario no existe o no está activo
                throw new UsernameNotFoundException("Usuario o email no encontrado o inactivo: " + usernameOrEmail);
            }
        }

        // Ya las consultas findByUserNameAndActivoTrue y findByEmailAndActivoTrue
        // se encargan de filtrar por activo = true. Si el usuario llega hasta aquí,
        // es porque está activo. No obstante, una verificación explícita no está de más
        // si se quiere un mensaje de error más específico en algún caso.
        if (!usuario.isActivo()) {
            // Este throw en teoría no debería alcanzarse si los findBy...AndActivoTrue funcionan correctamente,
            // pero sirve como una capa extra de seguridad.
            throw new UsernameNotFoundException("La cuenta del usuario está inactiva: " + usernameOrEmail);
        }


        // Construye y devuelve el objeto UserDetails de Spring Security
        // Asegúrate de que el rol se mapee correctamente, ej., ROLE_ADMIN, ROLE_USER
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(), // O el campo que uses como identificador principal en UserDetails (email, username)
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }
    // *** FIN MÉTODO loadUserByUsername ***

    @Transactional
    public UserDTO updateProfile(Long userId, UserProfileUpdateDTO updateDTO) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(userId);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        Usuario usuario = optionalUsuario.get();

        // Verificar si la cuenta está activa antes de permitir la actualización del perfil
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada y no puede ser modificada.");
        }

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

        if (updateDTO.getAddresses() != null) {
            Set<Direccion> direccionesEnviadasYActualizadas = new HashSet<>();

            for (DomicilioDTO dtoAddress : updateDTO.getAddresses()) {
                Direccion currentAddressEntity;

                if (dtoAddress.getId() != null) {
                    currentAddressEntity = usuario.getDirecciones().stream()
                            .filter(a -> a.getId() != null && a.getId().equals(dtoAddress.getId()))
                            .findFirst()
                            .orElse(null);

                    if (currentAddressEntity == null) {
                        currentAddressEntity = new Direccion();
                        currentAddressEntity.setUsuario(usuario);
                    }
                } else {
                    currentAddressEntity = new Direccion();
                    currentAddressEntity.setUsuario(usuario);
                }

                currentAddressEntity.setCalle(dtoAddress.getCalle());
                currentAddressEntity.setNumero(dtoAddress.getNumero());
                currentAddressEntity.setPiso(dtoAddress.getPiso());
                currentAddressEntity.setDepartamento(dtoAddress.getDepartamento());
                currentAddressEntity.setCp(dtoAddress.getCp());

                if (dtoAddress.getLocalidad() != null && dtoAddress.getLocalidad().getId() != null) {
                    Localidad localidad = localidadRepository.findById(dtoAddress.getLocalidad().getId())
                            .orElseThrow(() -> new RuntimeException("Localidad con ID " + dtoAddress.getLocalidad().getId() + " no encontrada."));
                    currentAddressEntity.setLocalidad(localidad);
                } else {
                    currentAddressEntity.setLocalidad(null);
                }

                direccionesEnviadasYActualizadas.add(currentAddressEntity);
            }

            List<Direccion> direccionesActualesDelUsuario = new ArrayList<>(usuario.getDirecciones());
            for (Direccion existingDireccion : direccionesActualesDelUsuario) {
                if (!direccionesEnviadasYActualizadas.contains(existingDireccion)) {
                    usuario.removeDireccion(existingDireccion);
                }
            }

            for (Direccion newOrUpdatedDireccion : direccionesEnviadasYActualizadas) {
                if (!usuario.getDirecciones().contains(newOrUpdatedDireccion)) {
                    usuario.addDireccion(newOrUpdatedDireccion);
                }
            }

        } else if (updateDTO.getAddresses() != null && updateDTO.getAddresses().isEmpty()) {
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
                // Intenta parsear el username como ID (si usas ID como username en UserDetails)
                userId = Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                // Si no es un ID, asume que es el email y busca por email
                // *** Usar findByEmailAndActivoTrue para asegurar que la cuenta está activa ***
                Usuario userByEmail = usuarioRepository.findByEmailAndActivoTrue(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("Usuario con email " + userDetails.getUsername() + " no encontrado o inactivo."));
                userId = userByEmail.getId();
            }
        } else if (principal instanceof Long) { // Si el principal ya es el ID directamente
            userId = (Long) principal;
        } else if (principal instanceof String) { // Si el principal es un String (ej. username)
            // *** Usar findByUserNameAndActivoTrue para asegurar que la cuenta está activa ***
            Usuario userByUsername = usuarioRepository.findByUserNameAndActivoTrue((String)principal)
                    .orElseThrow(() -> new RuntimeException("Usuario con username " + (String)principal + " no encontrado o inactivo."));
            userId = userByUsername.getId();
        }
        else {
            throw new IllegalStateException("Formato de principal de seguridad no soportado. No se pudo obtener el ID del usuario.");
        }
        if (userId == null) {
            throw new IllegalStateException("ID de usuario no encontrado en la autenticación.");
        }
        final Long finalUserId = userId;
        Usuario usuario = usuarioRepository.findById(finalUserId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + finalUserId + " no encontrado en la base de datos."));

        // *** IMPORTANTE: La verificación de estado activo ya se debería haber realizado al cargar el usuario
        //    en loadUserByUsername si se usa JWT, o aquí si se obtiene por ID directamente después de la autenticación.
        //    Si findById no está restringido a activos, esta línea es crucial.
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta del usuario está desactivada.");
        }
        // *****************************************************************************************

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
                .imagenUser(imagenDTO) // <--- ¡CAMBIO AQUI! profileImage -> imagenUser
                .build();
    }

    public DomicilioDTO mapToDomicilioDTO(Direccion direccion) {
        if (direccion == null) {
            return null;
        }
        LocalidadDTO localidadDTO = null;
        if (direccion.getLocalidad() != null) {
            ProvinciaDTO provinciaDTO = null;
            if (direccion.getLocalidad().getProvincia() != null) {
                provinciaDTO = new ProvinciaDTO(direccion.getLocalidad().getProvincia().getId(), direccion.getLocalidad().getProvincia().getNombre());
            }
            localidadDTO = new LocalidadDTO(direccion.getLocalidad().getId(), direccion.getLocalidad().getNombre(), provinciaDTO);
        }

        return DomicilioDTO.builder()
                .id(direccion.getId())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .piso(direccion.getPiso())
                .departamento(direccion.getDepartamento())
                .cp(direccion.getCp())
                .localidad(localidadDTO)
                .build();
    }

    private ImagenDTO mapToImagenDTO(Imagen imagen) {
        if (imagen == null) {
            return null;
        }
        return ImagenDTO.builder()
                .id(imagen.getId())
                .url(imagen.getDenominacion())
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

        // Verificar si la cuenta está activa antes de permitir la subida de imagen
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada y no puede ser modificada.");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path copyLocation = Paths.get(uploadDir + java.io.File.separator + uniqueFilename);

        try {
            Files.createDirectories(copyLocation.getParent());
        } catch (IOException e) {
            throw new IOException("No se pudo crear el directorio de subida: " + copyLocation.getParent(), e);
        }

        try {
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Error al guardar el archivo de imagen: " + uniqueFilename, e);
        }

        String baseUrl = "http://localhost:8080";
        String imageUrl = baseUrl + "/uploads/" + uniqueFilename;

        Imagen imagenUser = usuario.getImagenUser();
        if (imagenUser == null) {
            imagenUser = new Imagen();
        }
        imagenUser.setDenominacion(imageUrl);
        usuario.setImagenUser(imagenUser);
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

        // Verificar si la cuenta está activa antes de permitir la actualización de credenciales
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada y no puede ser modificada.");
        }

        if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña actual es requerida para confirmar los cambios.");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Contraseña actual incorrecta.");
        }

        boolean changesMade = false;

        // Usamos request.getNewEmail() porque tu DTO lo tiene así
        if (request.getNewEmail() != null && !request.getNewEmail().trim().isEmpty() && !request.getNewEmail().equals(usuario.getEmail())) {
            // Antes de cambiar el email, verificar si el nuevo email ya está en uso por una cuenta ACTIVA
            // *** Usar countByEmailAndActivoTrue para esta validación ***
            long activeUsersWithNewEmail = usuarioRepository.countByEmailAndActivoTrue(request.getNewEmail());
            if (activeUsersWithNewEmail > 0) {
                throw new IllegalArgumentException("El nuevo correo electrónico ya está en uso por otra cuenta activa.");
            }
            usuario.setEmail(request.getNewEmail());
            usuario.setUserName(request.getNewEmail()); // Actualizar username también si es el email
            changesMade = true;
        }

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            if (passwordEncoder.matches(request.getNewPassword(), usuario.getPassword())) {
                throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la actual.");
            }
            usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
            changesMade = true;
        }

        if (!changesMade) {
            System.out.println("DEBUG: No se realizaron cambios de email o contraseña.");
            return mapToUserDTO(usuario);
        }

        Usuario savedUsuario = usuarioRepository.save(usuario);
        System.out.println("DEBUG: Usuario con ID " + userId + " credenciales actualizadas y guardadas.");
        return mapToUserDTO(savedUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByUserName(String username) {
        // En este contexto, si se usa findByUserName, asume que es para buscar activos para operaciones normales.
        // Si necesitas buscar inactivos para admin, usa el método sin "AndActivoTrue" en el repositorio
        // y maneja el estado `activo` explícitamente.
        return usuarioRepository.findByUserNameAndActivoTrue(username);
    }

    @Transactional(readOnly = true)
    public Long getUserIdByUsernameOrEmail(String usernameOrEmail) {
        try {
            return Long.parseLong(usernameOrEmail);
        } catch (NumberFormatException e) {
            // Al buscar por email/username para obtener el ID, también debe ser de una cuenta activa.
            // *** Usar findByEmailAndActivoTrue o findByUserNameAndActivoTrue ***
            Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(usernameOrEmail)
                    .orElseGet(() -> usuarioRepository.findByUserNameAndActivoTrue(usernameOrEmail)
                            .orElseThrow(() -> new RuntimeException("Usuario con username/email " + usernameOrEmail + " no encontrado o inactivo.")));

            return usuario.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<Direccion> getDireccionesByUserId(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        // Opcional: Verificar si el usuario está activo antes de devolver direcciones
        if (!usuario.isActivo()) {
            throw new RuntimeException("No se pueden obtener direcciones de una cuenta desactivada.");
        }
        return List.copyOf(usuario.getDirecciones());
    }

    @Transactional
    public Direccion addDireccionToUser(Long userId, DomicilioDTO domicilioDTO) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        // Verificar si la cuenta está activa
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada y no puede añadir direcciones.");
        }

        Direccion newDireccion = new Direccion();
        newDireccion.setCalle(domicilioDTO.getCalle());
        newDireccion.setNumero(domicilioDTO.getNumero());
        newDireccion.setPiso(domicilioDTO.getPiso());
        newDireccion.setDepartamento(domicilioDTO.getDepartamento());
        newDireccion.setCp(domicilioDTO.getCp());

        if (domicilioDTO.getLocalidad() != null && domicilioDTO.getLocalidad().getId() != null) {
            Localidad localidad = localidadRepository.findById(domicilioDTO.getLocalidad().getId())
                    .orElseThrow(() -> new RuntimeException("Localidad con ID " + domicilioDTO.getLocalidad().getId() + " no encontrada."));
            newDireccion.setLocalidad(localidad);
        } else {
            throw new IllegalArgumentException("La dirección debe tener una Localidad válida con ID.");
        }

        usuario.addDireccion(newDireccion);
        usuarioRepository.save(usuario);
        return newDireccion;
    }

    @Transactional
    public Direccion updateDireccionForUser(Long userId, Long direccionId, DomicilioDTO updatedDomicilioDTO) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        // Verificar si la cuenta está activa
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada y no puede actualizar direcciones.");
        }

        Direccion existingDireccion = usuario.getDirecciones().stream()
                .filter(d -> d.getId().equals(direccionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + direccionId + " para el usuario " + userId));

        existingDireccion.setCalle(updatedDomicilioDTO.getCalle());
        existingDireccion.setNumero(updatedDomicilioDTO.getNumero());
        existingDireccion.setPiso(updatedDomicilioDTO.getPiso());
        existingDireccion.setDepartamento(updatedDomicilioDTO.getDepartamento());
        existingDireccion.setCp(updatedDomicilioDTO.getCp());

        if (updatedDomicilioDTO.getLocalidad() != null && updatedDomicilioDTO.getLocalidad().getId() != null) {
            Localidad localidad = localidadRepository.findById(updatedDomicilioDTO.getLocalidad().getId())
                    .orElseThrow(() -> new RuntimeException("Localidad con ID " + updatedDomicilioDTO.getLocalidad().getId() + " no encontrada."));
            existingDireccion.setLocalidad(localidad);
        } else {
            throw new IllegalArgumentException("La dirección debe tener una Localidad válida con ID.");
        }

        usuarioRepository.save(usuario);
        return existingDireccion;
    }

    @Transactional
    public void removeDireccionFromUser(Long userId, Long direccionId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        // Verificar si la cuenta está activa
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada y no puede eliminar direcciones.");
        }

        Direccion direccionToRemove = usuario.getDirecciones().stream()
                .filter(d -> d.getId().equals(direccionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + direccionId + " para el usuario " + userId));

        usuario.removeDireccion(direccionToRemove);
        usuarioRepository.save(usuario);
    }

    // --- MÉTODO PARA DESACTIVAR LA CUENTA (MODIFICADO) ---
    @Transactional
    public void deactivateAccount(Long userId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);
        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        Usuario usuario = usuarioOptional.get();

        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta ya está desactivada.");
        }

        usuario.setActivo(false);
        // Libera el email poniéndolo a NULL (asumiendo que tu columna 'email' permite NULL y no tiene UNIQUE)
        usuario.setEmail(null);

        // *** IMPORTANTE: Libera también el username para evitar errores de unicidad.
        // Genera un username único para el usuario desactivado.
        usuario.setUserName("deactivated_" + UUID.randomUUID().toString()); // Esto es más robusto
        // Si necesitas que el username mantenga el ID, puedes usar: "deactivated_" + UUID.randomUUID().toString() + "_" + usuario.getId();
        // Pero con el UUID solo ya es suficiente para la unicidad.

        usuarioRepository.save(usuario);
    }
    // --- FIN MÉTODO DESACTIVAR CUENTA ---

    // Este método ya estaba, solo que ahora se usa para obtener el ID de un usuario ACTIVO
    // para las operaciones de seguridad (ej. getCurrentUser, updateProfile)
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUserNameAndActivoTrue(String username) {
        return usuarioRepository.findByUserNameAndActivoTrue(username);
    }
}