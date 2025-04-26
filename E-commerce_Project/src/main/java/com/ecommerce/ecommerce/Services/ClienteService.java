package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Cliente;
import com.ecommerce.ecommerce.Entities.enums.Rol;
import com.ecommerce.ecommerce.Repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService extends BaseService<Cliente, Long> {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        super(clienteRepository);  // Llamada al constructor de la clase base
        this.clienteRepository = clienteRepository;
    }

    // Método para buscar un cliente por su usuario ID
    public Cliente findByUsuarioId(Long idUsuario) throws Exception {
        try {
            Optional<Cliente> clienteOptional = clienteRepository.findById(idUsuario);
            if (clienteOptional.isPresent()) {
                return clienteOptional.get();  // Retorna el cliente si lo encuentra
            } else {
                throw new Exception("Cliente no encontrado");
            }
        } catch (Exception e) {
            throw new Exception("Error al buscar cliente por ID de usuario: " + e.getMessage());
        }
    }

    // Método para buscar un cliente por imagen asociada
    public Cliente findByImagenPersonaId(Long idImagen) throws Exception {
        try {
            // Ajustamos el nombre del método que se llama al repositorio
            return clienteRepository.findByImagenUser_Id(idImagen);  // Usa el método de repositorio actualizado
        } catch (Exception e) {
            throw new Exception("Error al buscar cliente por ID de imagen: " + e.getMessage());
        }
    }

    @Override
    public Cliente crear(Cliente cliente) throws Exception {
        try {
            if (!isValidEmail(cliente.getEmail())) {
                throw new Exception("El correo electrónico no es válido.");
            }

            // Validación de DNI
            if (cliente.getDni() == null || cliente.getDni().toString().length() != 8) {
                throw new Exception("El DNI debe tener exactamente 8 caracteres.");
            }

            // Validación de rol
            if (cliente.getRol() == null || cliente.getRol() != Rol.CLIENTE) {
                throw new Exception("El rol del cliente no es válido.");
            }

            // Validar que el cliente tenga al menos una dirección
            if (cliente.getDirecciones() == null || cliente.getDirecciones().isEmpty()) {
                throw new Exception("El cliente debe tener al menos una dirección.");
            }

            // Crear el cliente
            return super.crear(cliente);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Cliente actualizar(Cliente cliente) throws Exception {
        try {
            // Verificar que el cliente exista
            Optional<Cliente> clienteExistente = clienteRepository.findById(cliente.getId());
            if (!clienteExistente.isPresent()) {
                throw new Exception("El cliente no existe.");
            }

            // Validación de correo electrónico
            if (!isValidEmail(cliente.getEmail())) {
                throw new Exception("El correo electrónico no es válido.");
            }

            // Validación de DNI
            if (cliente.getDni() == null || cliente.getDni().toString().length() != 8) {
                throw new Exception("El DNI debe tener exactamente 8 caracteres.");
            }

            // Validación de rol
            if (cliente.getRol() == null || cliente.getRol() != Rol.CLIENTE) {
                throw new Exception("El rol del cliente no es válido.");
            }

            // Validar que el cliente tenga al menos una dirección
            if (cliente.getDirecciones() == null || cliente.getDirecciones().isEmpty()) {
                throw new Exception("El cliente debe tener al menos una dirección.");
            }

            // Actualizar el cliente
            return super.actualizar(cliente);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void eliminar(Long id) throws Exception {
        try {
            // Verificar que el cliente exista antes de eliminar
            Optional<Cliente> clienteExistente = clienteRepository.findById(id);
            if (!clienteExistente.isPresent()) {
                throw new Exception("El cliente no existe.");
            }

            // Eliminar el cliente
            super.eliminar(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private boolean isValidEmail(String email) throws Exception {
        try {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            return email.matches(emailRegex);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
