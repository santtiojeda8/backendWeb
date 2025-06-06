package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Descuento;
import com.ecommerce.ecommerce.Repositories.DescuentosRepository;
import com.ecommerce.ecommerce.dto.DescuentoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // Import for BigDecimal
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DescuentosService extends BaseService<Descuento, Long> {

    private final DescuentosRepository descuentosRepository;

    public DescuentosService(DescuentosRepository descuentosRepository) {
        super(descuentosRepository); // Llama al constructor de BaseService
        this.descuentosRepository = descuentosRepository;
    }

    // --- Métodos de Mapeo (Entidad a DTO) ---
    // Make this public if you need to reuse it outside, e.g., in ProductoService
    public DescuentoDTO mapearDescuentoADTO(Descuento descuento) {
        if (descuento == null) {
            return null;
        }
        DescuentoDTO dto = new DescuentoDTO();
        dto.setId(descuento.getId());
        dto.setDenominacion(descuento.getDenominacion());
        dto.setFechaDesde(descuento.getFechaDesde());
        dto.setFechaHasta(descuento.getFechaHasta());
        dto.setHoraDesde(descuento.getHoraDesde());
        dto.setHoraHasta(descuento.getHoraHasta());
        dto.setDescripcionDescuento(descuento.getDescripcionDescuento());
        // Ensure that both entity and DTO use BigDecimal for promotional price
        dto.setPrecioPromocional(descuento.getPrecioPromocional());
        dto.setActivo(descuento.isActivo());
        return dto;
    }

    // --- Métodos de Mapeo (DTO a Entidad para Creación/Actualización) ---
    // Make this public if you need to reuse it outside, e.g., in testing
    public Descuento mapearDTOaDescuento(DescuentoDTO dto) {
        if (dto == null) {
            return null;
        }
        Descuento descuento = new Descuento();
        // ID is handled in update method logic. For creation, it's null.
        // If dto.getId() is passed for creation, JPA might try to update existing.
        // It's safer to not set ID here for creation and let JPA generate it.
        // For updates, the existing entity's ID is retained.
        if (dto.getId() != null) {
            descuento.setId(dto.getId());
        }
        descuento.setDenominacion(dto.getDenominacion());
        descuento.setFechaDesde(dto.getFechaDesde());
        descuento.setFechaHasta(dto.getFechaHasta());
        descuento.setHoraDesde(dto.getHoraDesde());
        descuento.setHoraHasta(dto.getHoraHasta());
        descuento.setDescripcionDescuento(dto.getDescripcionDescuento());
        // Ensure that both entity and DTO use BigDecimal for promotional price
        descuento.setPrecioPromocional(dto.getPrecioPromocional());
        descuento.setActivo(dto.isActivo());
        return descuento;
    }


    // --- MÉTODOS PÚBLICOS QUE TRABAJAN CON DTOs ---

    @Transactional(readOnly = true)
    public List<DescuentoDTO> listarDTOs() throws Exception {
        try {
            // super.listar() already fetches only active ones based on BaseRepository
            List<Descuento> descuentos = super.listar();
            return descuentos.stream()
                    .map(this::mapearDescuentoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al listar los descuentos (DTOs): " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al listar los descuentos: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public DescuentoDTO obtenerDescuentoDTOPorId(Long id) throws Exception {
        try {
            Descuento descuento = super.buscarPorId(id); // This will throw if not found or inactive
            return mapearDescuentoADTO(descuento);
        } catch (Exception e) {
            System.err.println("Error al obtener el descuento DTO por ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new Exception("No se pudo obtener el descuento por ID " + id + ": " + e.getMessage());
        }
    }

    @Transactional
    public DescuentoDTO crearDescuento(DescuentoDTO dto) throws Exception {
        // For creation, ensure ID is null to let JPA assign it
        if (dto.getId() != null) {
            throw new IllegalArgumentException("ID must be null for new discount creation.");
        }
        Descuento descuentoAcrear = mapearDTOaDescuento(dto);
        validarDescuento(descuentoAcrear); // Validate before saving
        try {
            // Set activo to true by default for new entities if not set by DTO or business rule
            if (!descuentoAcrear.isActivo()) {
                descuentoAcrear.setActivo(true);
            }
            Descuento savedDescuento = super.crear(descuentoAcrear); // BaseService.crear() ensures active=true
            return mapearDescuentoADTO(savedDescuento);
        } catch (Exception e) {
            System.err.println("Error al crear el descuento desde DTO: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al crear el descuento: " + e.getMessage());
        }
    }

    @Transactional
    public DescuentoDTO actualizarDescuento(Long id, DescuentoDTO dto) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("ID no puede ser nulo para la actualización.");
        }
        if (dto.getId() != null && !id.equals(dto.getId())) {
            // This check prevents cases where the path variable ID differs from the DTO body ID
            throw new IllegalArgumentException("El ID de la URL no coincide con el ID del cuerpo del DTO.");
        }

        Descuento descuentoExistente = super.buscarPorId(id); // This will throw if not found or inactive

        // Update properties from DTO to existing entity
        descuentoExistente.setDenominacion(dto.getDenominacion());
        descuentoExistente.setFechaDesde(dto.getFechaDesde());
        descuentoExistente.setFechaHasta(dto.getFechaHasta());
        descuentoExistente.setHoraDesde(dto.getHoraDesde());
        descuentoExistente.setHoraHasta(dto.getHoraHasta());
        descuentoExistente.setDescripcionDescuento(dto.getDescripcionDescuento());
        descuentoExistente.setPrecioPromocional(dto.getPrecioPromocional());
        descuentoExistente.setActivo(dto.isActivo()); // Allow updating active status

        validarDescuento(descuentoExistente); // Validate updated entity
        try {
            Descuento updatedDescuento = super.actualizar(descuentoExistente);
            return mapearDescuentoADTO(updatedDescuento);
        } catch (Exception e) {
            System.err.println("Error al actualizar el descuento desde DTO (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al actualizar el descuento: " + e.getMessage());
        }
    }

    // Soft delete method (from BaseService, public for direct call)
    @Transactional
    public void eliminarDescuento(Long id) throws Exception {
        try {
            super.eliminar(id); // Calls the soft delete method in BaseService
        } catch (Exception e) {
            System.err.println("Error al eliminar (desactivar) el descuento por ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al eliminar (desactivar) el descuento: " + e.getMessage());
        }
    }

    // New method to activate a discount
    @Transactional
    public DescuentoDTO activarDescuento(Long id) throws Exception {
        try {
            Descuento descuento = descuentosRepository.findById(id)
                    .orElseThrow(() -> new Exception("Descuento no encontrado con ID: " + id));

            if (descuento.isActivo()) {
                System.out.println("El descuento con ID: " + id + " ya está activo.");
                return mapearDescuentoADTO(descuento);
            }

            descuento.setActivo(true);
            Descuento activatedDescuento = descuentosRepository.save(descuento);
            return mapearDescuentoADTO(activatedDescuento);
        } catch (Exception e) {
            System.err.println("Error al activar descuento (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al activar descuento: " + e.getMessage());
        }
    }


    // --- Método de validación de descuento (se mantiene igual) ---
    private void validarDescuento(Descuento descuento) throws Exception {
        try {
            if (descuento.getDenominacion() == null || descuento.getDenominacion().trim().isEmpty()) {
                throw new IllegalArgumentException("La denominación no puede estar vacía.");
            }
            if (descuento.getFechaDesde() == null || descuento.getFechaHasta() == null) {
                throw new IllegalArgumentException("Las fechas de inicio y fin del descuento no pueden ser nulas.");
            }
            if (descuento.getFechaDesde().isAfter(descuento.getFechaHasta())) {
                throw new IllegalArgumentException("La fecha de inicio del descuento no puede ser posterior a la fecha de fin.");
            }
            // HoraDesde y HoraHasta pueden ser nulas si el descuento aplica todo el día
            if (descuento.getHoraDesde() != null && descuento.getHoraHasta() != null &&
                    descuento.getHoraDesde().isAfter(descuento.getHoraHasta())) {
                throw new IllegalArgumentException("La hora de inicio del descuento no puede ser posterior a la hora de fin.");
            }
            if (descuento.getPrecioPromocional() == null || descuento.getPrecioPromocional().compareTo(BigDecimal.ZERO) < 0 || descuento.getPrecioPromocional().compareTo(BigDecimal.ONE) > 0) {
                // Assuming precioPromocional is a factor (0.0 to 1.0)
                throw new IllegalArgumentException("El precio promocional debe ser un valor entre 0.0 y 1.0 (factor de descuento).");
            }
        } catch (IllegalArgumentException e) {
            // Catch specific IllegalArgumentExceptions and re-throw them without wrapping
            System.err.println("Error de validación: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado durante la validación del descuento: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error inesperado durante la validación del descuento: " + e.getMessage());
        }
    }
}