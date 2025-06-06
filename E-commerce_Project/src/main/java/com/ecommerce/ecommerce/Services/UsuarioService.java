package com.ecommerce.ecommerce.Services;

// ... (todas tus importaciones existentes)

import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Entities.Imagen;
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import com.ecommerce.ecommerce.Repositories.DireccionRepository;
import com.ecommerce.ecommerce.Repositories.LocalidadRepository;
import com.ecommerce.ecommerce.dto.DireccionDTO;
import com.ecommerce.ecommerce.dto.ImagenDTO;
import com.ecommerce.ecommerce.dto.LocalidadDTO;
import com.ecommerce.ecommerce.dto.ProvinciaDTO;
import com.ecommerce.ecommerce.dto.UpdateCredentialsRequest;
import com.ecommerce.ecommerce.dto.UserDTO;
import com.ecommerce.ecommerce.dto.UserProfileUpdateDTO;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- ¡Usar esta importación para @Transactional!
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Usuario usuario;

        Optional<Usuario> byUserName = usuarioRepository.findByUserNameAndActivoTrue(usernameOrEmail);

        if (byUserName.isPresent()) {
            usuario = byUserName.get();
        } else {
            Optional<Usuario> byEmail = usuarioRepository.findByEmailAndActivoTrue(usernameOrEmail);
            if (byEmail.isPresent()) {
                usuario = byEmail.get();
            } else {
                throw new UsernameNotFoundException("Usuario o email no encontrado o inactivo: " + usernameOrEmail);
            }
        }

        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("La cuenta del usuario está inactiva: " + usernameOrEmail);
        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }

    @Transactional
    public UserDTO updateProfile(Long userId, UserProfileUpdateDTO updateDTO) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(userId);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        Usuario usuario = optionalUsuario.get();

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
            Hibernate.initialize(usuario.getDirecciones()); // Asegurar que las direcciones estén cargadas

            Set<Direccion> direccionesEnviadasYActualizadas = new HashSet<>();

            for (DireccionDTO dtoAddress : updateDTO.getAddresses()) {
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
            Hibernate.initialize(usuario.getDirecciones());
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
                userId = Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                Usuario userByEmail = usuarioRepository.findByEmailAndActivoTrue(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("Usuario con email " + userDetails.getUsername() + " no encontrado o inactivo."));
                userId = userByEmail.getId();
            }
        } else if (principal instanceof Long) {
            userId = (Long) principal;
        } else if (principal instanceof String) {
            Usuario userByUsername = usuarioRepository.findByUserNameAndActivoTrue((String)principal)
                    .orElseThrow(() -> new RuntimeException("Usuario con username " + (String)principal + " no encontrado o inactivo."));
            userId = userByUsername.getId();
        } else {
            throw new IllegalStateException("Formato de principal de seguridad no soportado. No se pudo obtener el ID del usuario.");
        }
        if (userId == null) {
            throw new IllegalStateException("ID de usuario no encontrado en la autenticación.");
        }
        final Long finalUserId = userId;
        Usuario usuario = usuarioRepository.findById(finalUserId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + finalUserId + " no encontrado en la base de datos."));

        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta del usuario está desactivada.");
        }

        return mapToUserDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UserDTO mapToUserDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        List<DireccionDTO> direccionDTOS = null;
        if (usuario.getDirecciones() != null) {
            Hibernate.initialize(usuario.getDirecciones());
            direccionDTOS = usuario.getDirecciones().stream()
                    .map(this::mapToDomicilioDTO)
                    .collect(Collectors.toList());
        }

        ImagenDTO imagenDTO = null;
        if (usuario.getImagenUser() != null) {
            Hibernate.initialize(usuario.getImagenUser());
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
                .addresses(direccionDTOS)
                .role(usuario.getRol())
                .imagenUser(imagenDTO)
                .build();
    }

    @Transactional(readOnly = true)
    public DireccionDTO mapToDomicilioDTO(Direccion direccion) {
        if (direccion == null) {
            return null;
        }

        LocalidadDTO localidadDTO = null;
        if (direccion.getLocalidad() != null) {
            Hibernate.initialize(direccion.getLocalidad());

            ProvinciaDTO provinciaDTO = null;
            if (direccion.getLocalidad().getProvincia() != null) {
                Hibernate.initialize(direccion.getLocalidad().getProvincia());
                provinciaDTO = ProvinciaDTO.builder()
                        .id(direccion.getLocalidad().getProvincia().getId())
                        .nombre(direccion.getLocalidad().getProvincia().getNombre())
                        .build();
            }
            localidadDTO = LocalidadDTO.builder()
                    .id(direccion.getLocalidad().getId())
                    .nombre(direccion.getLocalidad().getNombre())
                    .provincia(provinciaDTO)
                    .build();
        }

        return DireccionDTO.builder()
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
                .url(imagen.getUrl())
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
        imagenUser.setUrl(imageUrl);
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

        if (request.getNewEmail() != null && !request.getNewEmail().trim().isEmpty() && !request.getNewEmail().equals(usuario.getEmail())) {
            long activeUsersWithNewEmail = usuarioRepository.countByEmailAndActivoTrue(request.getNewEmail());
            if (activeUsersWithNewEmail > 0) {
                throw new IllegalArgumentException("El nuevo correo electrónico ya está en uso por otra cuenta activa.");
            }
            usuario.setEmail(request.getNewEmail());
            usuario.setUserName(request.getNewEmail());
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

    // Este método ya lo tenías, solo que ahora se usa para obtener el ID de un usuario ACTIVO
    // para las operaciones de seguridad (ej. getCurrentUser, updateProfile)
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUserNameAndActivoTrue(String username) {
        return usuarioRepository.findByUserNameAndActivoTrue(username);
    }

    // Este es el método que debe ser único y lo usamos para obtener el ID del usuario autenticado.
    @Transactional(readOnly = true)
    public Long getUserIdByUsernameOrEmail(String usernameOrEmail) {
        try {
            return Long.parseLong(usernameOrEmail);
        } catch (NumberFormatException e) {
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

        if (!usuario.isActivo()) {
            throw new RuntimeException("No se pueden obtener direcciones de una cuenta desactivada.");
        }
        Hibernate.initialize(usuario.getDirecciones());
        return new ArrayList<>(usuario.getDirecciones());
    }

    @Transactional
    public Direccion addDireccionToUser(Long userId, DireccionDTO direccionDTO) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada y no puede añadir direcciones.");
        }

        Direccion newDireccion = new Direccion();
        newDireccion.setCalle(direccionDTO.getCalle());
        newDireccion.setNumero(direccionDTO.getNumero());
        newDireccion.setPiso(direccionDTO.getPiso());
        newDireccion.setDepartamento(direccionDTO.getDepartamento());
        newDireccion.setCp(direccionDTO.getCp());

        if (direccionDTO.getLocalidad() != null && direccionDTO.getLocalidad().getId() != null) {
            Localidad localidad = localidadRepository.findById(direccionDTO.getLocalidad().getId())
                    .orElseThrow(() -> new RuntimeException("Localidad con ID " + direccionDTO.getLocalidad().getId() + " no encontrada."));
            newDireccion.setLocalidad(localidad);
        } else {
            throw new IllegalArgumentException("La dirección debe tener una Localidad válida con ID.");
        }

        usuario.addDireccion(newDireccion);
        usuarioRepository.save(usuario);
        return newDireccion;
    }

    @Transactional
    public Direccion updateDireccionForUser(Long userId, Long direccionId, DireccionDTO updatedDireccionDTO) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada y no puede actualizar direcciones.");
        }

        Hibernate.initialize(usuario.getDirecciones());

        Direccion existingDireccion = usuario.getDirecciones().stream()
                .filter(d -> d.getId().equals(direccionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + direccionId + " para el usuario " + userId));

        existingDireccion.setCalle(updatedDireccionDTO.getCalle());
        existingDireccion.setNumero(updatedDireccionDTO.getNumero());
        existingDireccion.setPiso(updatedDireccionDTO.getPiso());
        existingDireccion.setDepartamento(updatedDireccionDTO.getDepartamento());
        existingDireccion.setCp(updatedDireccionDTO.getCp());

        if (updatedDireccionDTO.getLocalidad() != null && updatedDireccionDTO.getLocalidad().getId() != null) {
            Localidad localidad = localidadRepository.findById(updatedDireccionDTO.getLocalidad().getId())
                    .orElseThrow(() -> new RuntimeException("Localidad con ID " + updatedDireccionDTO.getLocalidad().getId() + " no encontrada."));
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
        if (!usuario.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada y no puede eliminar direcciones.");
        }

        Hibernate.initialize(usuario.getDirecciones());

        Direccion direccionToRemove = usuario.getDirecciones().stream()
                .filter(d -> d.getId().equals(direccionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + direccionId + " para el usuario " + userId));

        usuario.removeDireccion(direccionToRemove);
        usuarioRepository.save(usuario);
    }

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
        usuario.setEmail(null);
        usuario.setUserName("deactivated_" + UUID.randomUUID().toString());

        usuarioRepository.save(usuario);
    }
}