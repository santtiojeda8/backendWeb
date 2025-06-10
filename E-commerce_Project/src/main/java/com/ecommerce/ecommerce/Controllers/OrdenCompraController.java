package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.enums.EstadoOrdenCompra; // Asegúrate de que este enum esté en Entities.enums
import com.ecommerce.ecommerce.Services.OrdenCompraService;
import com.ecommerce.ecommerce.dto.OrdenCompraDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

// Importaciones para el Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/orden_compra")
public class OrdenCompraController extends BaseController<OrdenCompra, Long> {

    // DECLARAR EL LOGGER
    private static final Logger logger = LoggerFactory.getLogger(OrdenCompraController.class);

    private final OrdenCompraService ordenCompraService;

    @Autowired
    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        super(ordenCompraService);
        this.ordenCompraService = ordenCompraService;
    }

    // --- MÉTODOS ESPECÍFICOS PARA DTOs (con rutas únicas /dto) ---

    @GetMapping("/dto")
    public ResponseEntity<List<OrdenCompraDTO>> listarDTO() {
        try {
            List<OrdenCompraDTO> dtos = ordenCompraService.findAllDTO();
            if (dtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error al listar órdenes de compra DTOs: {}", e.getMessage(), e); // Usar logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<OrdenCompraDTO> buscarPorIdDTO(@PathVariable Long id) {
        try {
            OrdenCompraDTO dto = ordenCompraService.findByIdDTO(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            logger.error("Error al buscar orden de compra DTO por ID {}: {}", id, e.getMessage(), e); // Usar logger
            if (e.getMessage().contains("no encontrada o inactiva")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error("Error inesperado al buscar orden de compra DTO por ID {}: {}", id, e.getMessage(), e); // Usar logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/dto")
    public ResponseEntity<?> crearDTO(@RequestBody OrdenCompraDTO ordenCompraDTO) {
        try {
            OrdenCompra newEntity = ordenCompraService.saveOrdenCompraFromDTO(ordenCompraDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ordenCompraService.mapOrdenCompraToDTO(newEntity));
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Este bloque captura IllegalArgumentException e IllegalStateException
            logger.error("Error de validación al crear orden de compra desde DTO: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Este bloque capturará cualquier otra excepción que no sea las anteriores
            logger.error("Error inesperado al crear orden de compra desde DTO: {}", e.getMessage(), e); // Usar logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor al crear la orden de compra.");
        }
    }

    @PutMapping("/dto/{id}")
    public ResponseEntity<?> actualizarDTO(@PathVariable Long id, @RequestBody OrdenCompraDTO ordenCompraDTO) {
        try {
            OrdenCompra updatedEntity = ordenCompraService.updateOrdenCompraFromDTO(id, ordenCompraDTO);
            return ResponseEntity.ok(ordenCompraService.mapOrdenCompraToDTO(updatedEntity));
        } catch (IllegalArgumentException | IllegalStateException e) { // <-- Capturar las específicas primero
            logger.error("Error de validación al actualizar orden de compra ID {} desde DTO: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) { // <-- Luego las RuntimeException (que no sean las ya capturadas)
            logger.error("Error al actualizar orden de compra ID {}: {}", id, e.getMessage(), e);
            if (e.getMessage().contains("no encontrada o inactiva")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) { // <-- Finalmente, cualquier otra Exception
            logger.error("Error inesperado al actualizar orden de compra ID {} desde DTO: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor al actualizar la orden de compra.");
        }
    }

    // --- Otros métodos específicos (eliminar, por usuario, por fecha) ---


    @GetMapping("/fecha")
    public ResponseEntity<?> obtenerPorFecha(@RequestParam("fecha") String fecha) {
        try {
            LocalDateTime fechaCompra = LocalDateTime.parse(fecha);
            List<OrdenCompraDTO> dtos = ordenCompraService.obtenerPorFecha(fechaCompra);
            if (dtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(dtos);
        } catch (DateTimeParseException e) {
            logger.error("Error de formato de fecha al obtener órdenes de compra: {}. Formato esperado: yyyy-MM-dd'T'HH:mm:ss. Error: {}", fecha, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de fecha inválido. Use yyyy-MM-dd'T'HH:mm:ss");
        } catch (Exception e) {
            logger.error("Error al obtener órdenes de compra por fecha {}: {}", fecha, e.getMessage(), e); // Usar logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor al obtener las órdenes por fecha.");
        }
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<?> obtenerPorUsuarioDTO(@PathVariable Long userId) {
        try {
            List<OrdenCompraDTO> dtos = ordenCompraService.obtenerPorUsuarioDTO(userId);
            if (dtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            logger.error("Error al obtener órdenes de compra por usuario {}: {}", userId, e.getMessage(), e); // Usar logger
            if (e.getMessage().contains("No se encontraron órdenes de compra activas")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al obtener órdenes de compra por usuario {}: {}", userId, e.getMessage(), e); // Usar logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor al obtener las órdenes por usuario.");
        }
    }

    // --- NUEVO ENDPOINT PARA ACTUALIZAR ESTADO DE ORDEN (POST-MERCADOPAGO) ---
    @PutMapping("/{ordenId}/estado")
    public ResponseEntity<?> actualizarEstadoOrden(
            @PathVariable Long ordenId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String estadoStr = requestBody.get("estado");
            String mpPaymentId = requestBody.get("mpPaymentId");

            if (estadoStr == null || estadoStr.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El estado de la orden es obligatorio.");
            }
            if (mpPaymentId == null || mpPaymentId.isEmpty()) {
                logger.warn("Se intenta actualizar la orden {} sin un ID de pago de Mercado Pago. Estado: {}", ordenId, estadoStr);
            }

            EstadoOrdenCompra nuevoEstado = EstadoOrdenCompra.valueOf(estadoStr.toUpperCase());

            OrdenCompra updatedOrden = ordenCompraService.actualizarEstadoOrdenYStock(ordenId, nuevoEstado, mpPaymentId);

            return ResponseEntity.ok(ordenCompraService.mapOrdenCompraToDTO(updatedOrden));
        } catch (IllegalArgumentException e) { // Para errores de enum.valueOf() o validaciones del servicio
            logger.error("Error de validación al actualizar estado de orden {}: {}", ordenId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) { // Si el servicio lanza RuntimeException (ej. Orden no encontrada)
            logger.error("Error al actualizar estado de orden {}: {}", ordenId, e.getMessage(), e);
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar estado de orden {}: {}", ordenId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor al actualizar el estado de la orden.");
        }
    }
}