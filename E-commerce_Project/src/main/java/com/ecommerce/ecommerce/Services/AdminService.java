package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Admin;
import com.ecommerce.ecommerce.Entities.enums.Rol;
import com.ecommerce.ecommerce.Repositories.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@Service
public class AdminService extends BaseService<Admin, Long> {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        super(adminRepository);  // Llamada al constructor de la clase base
        this.adminRepository = adminRepository;
    }

    // Método para buscar por nombre de usuario
    public Admin findByUserName(String userName) throws Exception {
        try {
            return adminRepository.findByUserName(userName);
        } catch (Exception e) {
            throw new Exception("Error al buscar el admin: " + e.getMessage());
        }
    }

    // Método para buscar por imagen de usuario
    public Admin findByImagenUserId(Long idImagen) throws Exception {
        try {
            return adminRepository.findByImagenUserId(idImagen);  // Este método utiliza el id de la imagen asociada
        } catch (Exception e) {
            throw new Exception("Error al buscar por imagen de usuario: " + e.getMessage());
        }
    }

    @Override
    public Admin crear(Admin admin) throws Exception {
        try {
            // Validar que el nombre de usuario sea único
            if (adminRepository.findByUserName(admin.getUserName()) != null) {
                throw new Exception("El nombre de usuario ya está registrado.");
            }

            // Validación del rol directamente
            if (admin.getRol() == null || (admin.getRol() != Rol.ADMIN)) {
                throw new Exception("El rol del admin no es válido.");
            }

            // Validación de contraseña
            if (admin.getPassword().length() < 8) {
                throw new Exception("La contraseña debe tener al menos 8 caracteres.");
            }

            // Crear el admin
            return super.crear(admin);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Admin actualizar(Admin admin) throws Exception {
        try {
            if (admin.getRol() == null || !isValidRol(admin.getRol())) {
                throw new Exception("El rol del admin no es válido.");
            }

            // Validación de estado de admin
            if (admin.getActivo() == null || !admin.getActivo()) {
                throw new Exception("El administrador no está activo.");
            }

            // Validación de contraseña
            if (admin.getPassword().length() < 8) {
                throw new Exception("La contraseña debe tener al menos 8 caracteres.");
            }

            // Actualizar el admin
            return super.actualizar(admin);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void eliminar(Long id) throws Exception {
        try {
            Optional<Admin> adminExistente = adminRepository.findById(id);
            if (!adminExistente.isPresent()) {
                throw new Exception("El admin no existe.");
            }

            // Eliminar el admin
            super.eliminar(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private boolean isValidRol(Rol rol) throws Exception {
        try {
            // Asegurarse de que el rol esté entre los roles permitidos
            return rol == Rol.ADMIN;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
