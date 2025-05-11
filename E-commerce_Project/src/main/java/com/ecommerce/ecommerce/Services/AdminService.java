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
    public Admin findByUserName(String userName) throws Exception { // Este método está bien nombrado para tu repo
        try {
            // Asegúrate que el método en el repo devuelva Optional<Admin> si es posible
            // Y manejar el Optional aquí si lo hace
            // Por ahora, asumimos que puede devolver null si no lo encuentra
            return adminRepository.findByUserName(userName);
        } catch (Exception e) {
            throw new Exception("Error al buscar el admin: " + e.getMessage());
        }
    }

    // Método para buscar por imagen de usuario
    public Admin findByImagenUserId(Long idImagen) throws Exception {
        try {
            return adminRepository.findByImagenUserId(idImagen);
        } catch (Exception e) {
            throw new Exception("Error al buscar por imagen de usuario: " + e.getMessage());
        }
    }

    @Override
    public Admin crear(Admin admin) throws Exception {
        try {
            // Validar que el nombre de usuario sea único
            // >>> CAMBIAR admin.getUserName() a admin.getUsername() <<<
            if (adminRepository.findByUserName(admin.getUsername()) != null) {
                throw new Exception("El nombre de usuario ya está registrado.");
            }

            // Validación del rol directamente
            if (admin.getRol() == null || (admin.getRol() != Rol.ADMIN)) {
                throw new Exception("El rol del admin no es válido.");
            }

            // Validación de contraseña - Nota: Aquí solo validas longitud, NO encriptas
            // La encriptación se hace en AuthService durante el registro principal.
            // Si este método 'crear' se usa fuera del flujo de registro de AuthService, deberías encriptar aquí también.
            if (admin.getPassword() != null && admin.getPassword().length() < 8) {
                throw new Exception("La contraseña debe tener al menos 8 caracteres.");
            }
            // >>> IMPORTANTE: La encriptación de la contraseña DEBE hacerse antes de guardar si este método se usa para crear admins directamente <<<


            // Crear el admin
            return super.crear(admin);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // ... resto de métodos (actualizar, eliminar, isValidRol) ...
    @Override
    public Admin actualizar(Admin admin) throws Exception {
        try {
            // Si permites actualizar el username o password aquí, necesitas validación adicional
            // y posiblemente encriptar la nueva contraseña si cambia.
            // >>> CAMBIAR admin.getUserName() a admin.getUsername() si se usa en validaciones/búsquedas aquí <<<
            // Ejemplo (si se usa en una búsqueda de unicidad al actualizar):
            // Admin existingAdmin = adminRepository.findByUserName(admin.getUsername());
            // if (existingAdmin != null && !existingAdmin.getId().equals(admin.getId())) {
            //     throw new Exception("El nombre de usuario ya está registrado por otro admin.");
            // }


            if (admin.getRol() == null || !isValidRol(admin.getRol())) {
                throw new Exception("El rol del admin no es válido.");
            }

            // Validación de estado de admin
            if (admin.getActivo() == null || !admin.getActivo()) {
                throw new Exception("El administrador no está activo.");
            }

            // Validación de contraseña - Similar al crear, esto valida longitud, NO encripta
            if (admin.getPassword() != null && admin.getPassword().length() < 8) {
                throw new Exception("La contraseña debe tener al menos 8 caracteres.");
            }
            // >>> IMPORTANTE: Si se actualiza la contraseña aquí, DEBE encriptarse antes de llamar a super.actualizar() <<<


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