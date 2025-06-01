package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Entities.Provincia; // Asegurarse de que esta entidad sea la correcta
import com.ecommerce.ecommerce.Repositories.LocalidadRepository;
import com.ecommerce.ecommerce.Repositories.ProvinciaRepository;
import com.ecommerce.ecommerce.dto.LocalidadDTO; // Importamos LocalidadDTO
import com.ecommerce.ecommerce.dto.ProvinciaDTO; // Importamos ProvinciaDTO
import org.hibernate.Hibernate; // Importante para inicializar relaciones Lazy
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para el contexto de Hibernate

import java.util.List;
import java.util.Optional; // Necesario para Optional

@Service
public class LocalidadService extends BaseService<Localidad, Long> {

    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;

    public LocalidadService(LocalidadRepository localidadRepository, ProvinciaRepository provinciaRepository) {
        super(localidadRepository);
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
    }

    // Asegurarse de que este método esté transaccional para cargar las Localidades
    @Transactional(readOnly = true)
    public List<Localidad> findByProvinciaId(Long provinciaId) throws Exception {
        try {
            // Opcional: verificar que la provincia exista. Si es Lazy, se carga aquí.
            Optional<Provincia> provinciaOptional = provinciaRepository.findById(provinciaId);
            if (provinciaOptional.isEmpty()) {
                throw new Exception("Provincia no encontrada con ID: " + provinciaId);
            }
            // Puedes usar un fetch join en el repositorio para cargar la provincia junto con la localidad
            // o confiar en que el mapToLocalidadDTO la inicializará dentro de esta transacción.
            return localidadRepository.findByProvinciaId(provinciaId);
        } catch (Exception e) {
            throw new Exception("Error al obtener localidades para la provincia con ID: " + provinciaId + ". " + e.getMessage());
        }
    }

    /**
     * Método para mapear una entidad Localidad a un LocalidadDTO.
     * Este método debe ser llamado DENTRO de una transacción activa para asegurar
     * que las relaciones LAZY (como 'provincia') puedan ser inicializadas.
     */
    @Transactional(readOnly = true) // Crucial para la carga Lazy
    public LocalidadDTO mapToLocalidadDTO(Localidad localidad) {
        if (localidad == null) {
            return null;
        }

        ProvinciaDTO provinciaDTO = null;
        // Si la relación 'provincia' en la entidad Localidad es LAZY (que es lo más probable),
        // necesitamos inicializarla ANTES de acceder a sus propiedades, si estamos fuera del contexto de persistencia.
        // Al marcar mapToLocalidadDTO con @Transactional(readOnly=true), aseguramos que la sesión está abierta.
        // `Hibernate.initialize` fuerza la carga del proxy.
        if (localidad.getProvincia() != null) {
            Hibernate.initialize(localidad.getProvincia()); // Fuerza la inicialización de la provincia
            provinciaDTO = ProvinciaDTO.builder()
                    .id(localidad.getProvincia().getId())
                    .nombre(localidad.getProvincia().getNombre())
                    .build();
        }

        return LocalidadDTO.builder()
                .id(localidad.getId())
                .nombre(localidad.getNombre())
                .provincia(provinciaDTO) // Asignamos el DTO de Provincia
                .build();
    }
}