package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Descuento;
import com.ecommerce.ecommerce.Repositories.DescuentosRepository;
import com.ecommerce.ecommerce.dto.DescuentoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DescuentosService extends BaseService<Descuento, Long> {

    private final DescuentosRepository descuentosRepository;

    public DescuentosService(DescuentosRepository descuentosRepository) {
        super(descuentosRepository);
        this.descuentosRepository = descuentosRepository;
    }

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
        dto.setPrecioPromocional(descuento.getPrecioPromocional());
        dto.setActivo(descuento.isActivo());
        return dto;
    }

    public Descuento mapearDTOaDescuento(DescuentoDTO dto) {
        if (dto == null) {
            return null;
        }
        Descuento descuento = new Descuento();
        if (dto.getId() != null) {
            descuento.setId(dto.getId());
        }
        descuento.setDenominacion(dto.getDenominacion());
        descuento.setFechaDesde(dto.getFechaDesde());
        descuento.setFechaHasta(dto.getFechaHasta());
        descuento.setHoraDesde(dto.getHoraDesde());
        descuento.setHoraHasta(dto.getHoraHasta());
        descuento.setDescripcionDescuento(dto.getDescripcionDescuento());
        descuento.setPrecioPromocional(dto.getPrecioPromocional());
        descuento.setActivo(dto.isActivo());
        return descuento;
    }


    // --- MÉTODOS PÚBLICOS QUE TRABAJAN CON DTOs ---

    @Transactional(readOnly = true)
    public List<DescuentoDTO> listarDTOs() throws Exception {
        try {
            // Este método ahora debe llamar a findAll() en BaseService
            // para obtener TODOS los descuentos (activos e inactivos) para el panel de administración
            List<Descuento> descuentos = super.findAll(); // <--- CAMBIO IMPORTANTE AQUÍ
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
            // Usa buscarPorIdIncluyendoInactivos para permitir ver descuentos inactivos en edición
            Descuento descuento = super.buscarPorIdIncluyendoInactivos(id); // <--- CAMBIO IMPORTANTE AQUÍ
            return mapearDescuentoADTO(descuento);
        } catch (Exception e) {
            System.err.println("Error al obtener el descuento DTO por ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new Exception("No se pudo obtener el descuento por ID " + id + ": " + e.getMessage());
        }
    }

    @Transactional
    public DescuentoDTO crearDescuento(DescuentoDTO dto) throws Exception {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("ID must be null for new discount creation.");
        }
        Descuento descuentoAcrear = mapearDTOaDescuento(dto);
        // La validación de fechas/horas se hará en el frontend.
        // Aquí solo validamos reglas de negocio intrínsecas a la entidad si es necesario.
        validarDescuento(descuentoAcrear); // Validar antes de guardar
        try {
            // super.crear() ya se encarga de setActivo(true)
            Descuento savedDescuento = super.crear(descuentoAcrear);
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
            throw new IllegalArgumentException("El ID de la URL no coincide con el ID del cuerpo del DTO.");
        }

        // Buscar el descuento incluyendo inactivos para poder actualizar también los inactivos
        Descuento descuentoExistente = super.buscarPorIdIncluyendoInactivos(id); // <--- CAMBIO IMPORTANTE AQUÍ

        descuentoExistente.setDenominacion(dto.getDenominacion());
        descuentoExistente.setFechaDesde(dto.getFechaDesde());
        descuentoExistente.setFechaHasta(dto.getFechaHasta());
        descuentoExistente.setHoraDesde(dto.getHoraDesde());
        descuentoExistente.setHoraHasta(dto.getHoraHasta());
        descuentoExistente.setDescripcionDescuento(dto.getDescripcionDescuento());
        descuentoExistente.setPrecioPromocional(dto.getPrecioPromocional());
        descuentoExistente.setActivo(dto.isActivo());

        validarDescuento(descuentoExistente); // Validar la entidad actualizada
        try {
            Descuento updatedDescuento = super.actualizar(descuentoExistente);
            return mapearDescuentoADTO(updatedDescuento);
        } catch (Exception e) {
            System.err.println("Error al actualizar el descuento desde DTO (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al actualizar el descuento: " + e.getMessage());
        }
    }

    /**
     * Mapea y llama al toggleStatus del BaseService.
     * Este es el método que usará el frontend para cambiar el estado activo/inactivo.
     * @param id El ID del descuento.
     * @param currentStatus El estado actual del descuento (true si está activo, false si está inactivo).
     * @return El DTO del descuento actualizado con su nuevo estado.
     * @throws Exception Si el descuento no se encuentra o hay un error.
     */
    @Transactional
    public DescuentoDTO toggleDiscountStatus(Long id, boolean currentStatus) throws Exception {
        try {
            // Llama al método toggleStatus del BaseService que invierte el estado 'activo'
            Descuento updatedDescuento = super.toggleStatus(id, currentStatus);
            return mapearDescuentoADTO(updatedDescuento);
        } catch (Exception e) {
            System.err.println("Error al cambiar el estado del descuento (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al cambiar el estado del descuento: " + e.getMessage());
        }
    }

    // ELIMINAMOS el método eliminarDescuento que llamaba a super.eliminar(id);
    // Ahora, cualquier "eliminación" lógica se hará a través de toggleDiscountStatus(id, true)
    // es decir, un toggle que desactiva el descuento.

    // El método 'activarDescuento' ahora es redundante si usas toggleDiscountStatus
    // ya que toggleDiscountStatus(id, false) hará lo mismo.
    // Lo comento, pero puedes decidir si quieres mantenerlo por claridad o eliminarlo.
    /*
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
    */

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
            if (descuento.getHoraDesde() != null && descuento.getHoraHasta() != null &&
                    (descuento.getFechaDesde().isEqual(descuento.getFechaHasta())) && // Solo compara horas si las fechas son las mismas
                    (descuento.getHoraDesde().isAfter(descuento.getHoraHasta()))) {
                throw new IllegalArgumentException("La hora de inicio del descuento no puede ser posterior a la hora de fin en el mismo día.");
            }
            // Validar que el precio promocional esté entre 0 y 1 (factor de descuento)
            if (descuento.getPrecioPromocional() == null ||
                    descuento.getPrecioPromocional().compareTo(BigDecimal.ZERO) < 0 ||
                    descuento.getPrecioPromocional().compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("El precio promocional debe ser un valor entre 0.0 y 1.0 (factor de descuento).");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado durante la validación del descuento: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error inesperado durante la validación del descuento: " + e.getMessage());
        }
    }
}