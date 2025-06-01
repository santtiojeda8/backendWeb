package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Services.OrdenCompraDetalleService;
import com.ecommerce.ecommerce.dto.OrdenCompraDetalleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory

import java.util.List;

@RestController
@RequestMapping("/orden_compra_detalle")
public class OrdenCompraDetalleController extends BaseController<OrdenCompraDetalle, Long> {

    private static final Logger logger = LoggerFactory.getLogger(OrdenCompraDetalleController.class);

    private final OrdenCompraDetalleService ordenCompraDetalleService;

    @Autowired
    public OrdenCompraDetalleController(OrdenCompraDetalleService ordenCompraDetalleService) {
        super(ordenCompraDetalleService);
        this.ordenCompraDetalleService = ordenCompraDetalleService;
    }

    @GetMapping("/dto")
    public ResponseEntity<List<OrdenCompraDetalleDTO>> listarDetallesDTO() {
        try {
            List<OrdenCompraDetalleDTO> dtos = ordenCompraDetalleService.findAllDTO();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error al listar detalles de orden de compra DTOs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<OrdenCompraDetalleDTO> buscarDetallePorIdDTO(@PathVariable Long id) {
        try {
            OrdenCompraDetalleDTO dto = ordenCompraDetalleService.findByIdDTO(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("Error al buscar detalle de orden de compra DTO por ID {}: {}", id, e.getMessage(), e);
            if (e.getMessage().contains("no encontrado o inactivo")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/dto")
    public ResponseEntity<OrdenCompraDetalleDTO> crearDetalleDesdeDTO(@RequestBody OrdenCompraDetalleDTO ordenCompraDetalleDTO) {
        try {
            OrdenCompraDetalle newEntity = ordenCompraDetalleService.saveOrdenCompraDetalleFromDTO(ordenCompraDetalleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ordenCompraDetalleService.mapOrdenCompraDetalleToDTO(newEntity));
        } catch (Exception e) {
            logger.error("Error al crear detalle de orden de compra desde DTO: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/dto/{id}")
    public ResponseEntity<OrdenCompraDetalleDTO> actualizarDetalleDesdeDTO(@PathVariable Long id, @RequestBody OrdenCompraDetalleDTO ordenCompraDetalleDTO) {
        try {
            OrdenCompraDetalle updatedEntity = ordenCompraDetalleService.updateOrdenCompraDetalleFromDTO(id, ordenCompraDetalleDTO);
            return ResponseEntity.ok(ordenCompraDetalleService.mapOrdenCompraDetalleToDTO(updatedEntity));
        } catch (Exception e) {
            logger.error("Error al actualizar detalle de orden de compra ID {} desde DTO: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<OrdenCompraDetalleDTO>> obtenerDetallesPorOrdenId(@PathVariable Long ordenId) {
        try {
            List<OrdenCompraDetalleDTO> dtos = ordenCompraDetalleService.findByOrdenCompraId(ordenId);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error al obtener detalles de orden de compra por ID de orden {}: {}", ordenId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/producto_detalle/{productoDetalleId}")
    public ResponseEntity<List<OrdenCompraDetalleDTO>> obtenerDetallesPorProductoDetalleId(@PathVariable Long productoDetalleId) {
        try {
            List<OrdenCompraDetalleDTO> dtos = ordenCompraDetalleService.findByProductoDetalleId(productoDetalleId);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error al obtener detalles de orden de compra por ID de producto detalle {}: {}", productoDetalleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            ordenCompraDetalleService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            logger.error("Error al eliminar lógicamente detalle de orden de compra ID {}: {}", id, e.getMessage(), e);
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}