package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Repositories.DireccionRepository;
import com.ecommerce.ecommerce.dto.DireccionDTO;    // ¡Importamos DireccionDTO!
import com.ecommerce.ecommerce.dto.LocalidadDTO;    // ¡Importamos LocalidadDTO!
import com.ecommerce.ecommerce.dto.ProvinciaDTO;    // ¡Importamos ProvinciaDTO!
import org.hibernate.Hibernate; // Importante para inicializar relaciones Lazy
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para el contexto de Hibernate

import java.util.List;

@Service
public class DireccionService extends BaseService<Direccion, Long> {

    private final DireccionRepository direccionRepository; // Renombrado a private final para inyección por constructor

    @Autowired // Es buena práctica usar constructor injection para @Autowired
    public DireccionService(DireccionRepository direccionRepository) {
        super(direccionRepository);
        this.direccionRepository = direccionRepository;
    }

    // Asegurarse de que este método esté transaccional para cargar las Direcciones
    @Transactional(readOnly = true)
    public List<Direccion> listarPorLocalidad(Long idLocalidad) throws Exception {
        try {
            return direccionRepository.findAllByLocalidadId(idLocalidad);
        } catch (Exception e) {
            throw new Exception("Error al obtener direcciones por localidad con ID: " + idLocalidad + ". " + e.getMessage());
        }
    }

    // Asegurarse de que este método esté transaccional para cargar las Direcciones
    @Transactional(readOnly = true)
    public List<Direccion> listarPorClientesID(Long idCliente) throws Exception {
        try {
            return direccionRepository.findAllByClientesAndId(idCliente);
        } catch (Exception e) {
            throw new Exception("Error al obtener direcciones por cliente con ID: " + idCliente + ". " + e.getMessage());
        }
    }

    /**
     * Método para mapear una entidad Direccion a un DireccionDTO.
     * Este método debe ser llamado DENTRO de una transacción activa para asegurar
     * que las relaciones LAZY (como 'localidad' y 'provincia') puedan ser inicializadas.
     */
    @Transactional(readOnly = true) // Crucial para la carga Lazy
    public DireccionDTO mapToDireccionDTO(Direccion direccion) {
        if (direccion == null) {
            return null;
        }

        LocalidadDTO localidadDTO = null;
        // Si la relación 'localidad' en la entidad Direccion es LAZY, inicialízala aquí
        if (direccion.getLocalidad() != null) {
            Hibernate.initialize(direccion.getLocalidad()); // Fuerza la inicialización de la localidad

            ProvinciaDTO provinciaDTO = null;
            // Si la relación 'provincia' en la entidad Localidad es LAZY, inicialízala también
            if (direccion.getLocalidad().getProvincia() != null) {
                Hibernate.initialize(direccion.getLocalidad().getProvincia()); // Fuerza la inicialización de la provincia
                provinciaDTO = ProvinciaDTO.builder()
                        .id(direccion.getLocalidad().getProvincia().getId())
                        .nombre(direccion.getLocalidad().getProvincia().getNombre())
                        .build();
            }
            localidadDTO = LocalidadDTO.builder()
                    .id(direccion.getLocalidad().getId())
                    .nombre(direccion.getLocalidad().getNombre())
                    .provincia(provinciaDTO) // Asignamos el DTO de Provincia
                    .build();
        }

        return DireccionDTO.builder()
                .id(direccion.getId())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .piso(direccion.getPiso())
                .departamento(direccion.getDepartamento())
                .cp(direccion.getCp())
                .localidad(localidadDTO) // Asignamos el DTO de Localidad
                .build();
    }
}