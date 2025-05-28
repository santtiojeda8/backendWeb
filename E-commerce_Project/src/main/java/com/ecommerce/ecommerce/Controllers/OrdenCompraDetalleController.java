package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Services.OrdenCompraDetalleService;
import com.ecommerce.ecommerce.dto.OrdenCompraDetalleDTO; // Importa tu DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orden_compra_detalle")
public class OrdenCompraDetalleController extends BaseController<OrdenCompraDetalle, Long> {

    private final OrdenCompraDetalleService ordenCompraDetalleService;

    @Autowired
    public OrdenCompraDetalleController(OrdenCompraDetalleService ordenCompraDetalleService) {
        super(ordenCompraDetalleService); // Pasa el servicio al BaseController
        this.ordenCompraDetalleService = ordenCompraDetalleService; // Guarda una referencia para usar sus métodos DTO
    }

    // --- MÉTODOS ESPECÍFICOS PARA DTOs (con RUTAS ÚNICAS para evitar conflictos) ---

    // Este es un NUEVO ENDPOINT para listar DTOs, no sobrescribe el de BaseController.
    // Ruta: GET /orden_compra_detalle/dto
    @GetMapping("/dto")
    public ResponseEntity<List<OrdenCompraDetalleDTO>> listarDetallesDTO() {
        try {
            List<OrdenCompraDetalleDTO> dtos = ordenCompraDetalleService.findAllDTO();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("Error al listar detalles de orden de compra DTOs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Este es un NUEVO ENDPOINT para buscar DTOs por ID, no sobrescribe el de BaseController.
    // Ruta: GET /orden_compra_detalle/dto/{id}
    @GetMapping("/dto/{id}")
    public ResponseEntity<OrdenCompraDetalleDTO> buscarDetallePorIdDTO(@PathVariable Long id) {
        try {
            OrdenCompraDetalleDTO dto = ordenCompraDetalleService.findByIdDTO(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.err.println("Error al buscar detalle de orden de compra DTO por ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrado o inactivo")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Este es un NUEVO ENDPOINT para crear DTOs, no sobrescribe el de BaseController.
    // Ruta: POST /orden_compra_detalle/dto
    @PostMapping("/dto")
    public ResponseEntity<OrdenCompraDetalleDTO> crearDetalleDesdeDTO(@RequestBody OrdenCompraDetalleDTO ordenCompraDetalleDTO) {
        try {
            OrdenCompraDetalle newEntity = ordenCompraDetalleService.saveOrdenCompraDetalleFromDTO(ordenCompraDetalleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ordenCompraDetalleService.mapOrdenCompraDetalleToDTO(newEntity));
        } catch (Exception e) {
            System.err.println("Error al crear detalle de orden de compra desde DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Este es un NUEVO ENDPOINT para actualizar DTOs, no sobrescribe el de BaseController.
    // Ruta: PUT /orden_compra_detalle/dto/{id}
    @PutMapping("/dto/{id}")
    public ResponseEntity<OrdenCompraDetalleDTO> actualizarDetalleDesdeDTO(@PathVariable Long id, @RequestBody OrdenCompraDetalleDTO ordenCompraDetalleDTO) {
        try {
            OrdenCompraDetalle updatedEntity = ordenCompraDetalleService.updateOrdenCompraDetalleFromDTO(id, ordenCompraDetalleDTO);
            return ResponseEntity.ok(ordenCompraDetalleService.mapOrdenCompraDetalleToDTO(updatedEntity));
        } catch (Exception e) {
            System.err.println("Error al actualizar detalle de orden de compra ID " + id + " desde DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // --- Métodos específicos de lógica de negocio (no DTOs, no heredados) ---

    // Ruta: GET /orden_compra_detalle/orden/{ordenId}
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<OrdenCompraDetalleDTO>> obtenerDetallesPorOrdenId(@PathVariable Long ordenId) {
        try {
            List<OrdenCompraDetalleDTO> dtos = ordenCompraDetalleService.findByOrdenCompraId(ordenId);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("Error al obtener detalles de orden de compra por ID de orden " + ordenId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Ruta: GET /orden_compra_detalle/producto_detalle/{productoDetalleId}
    @GetMapping("/producto_detalle/{productoDetalleId}")
    public ResponseEntity<List<OrdenCompraDetalleDTO>> obtenerDetallesPorProductoDetalleId(@PathVariable Long productoDetalleId) {
        try {
            List<OrdenCompraDetalleDTO> dtos = ordenCompraDetalleService.findByProductoDetalleId(productoDetalleId);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("Error al obtener detalles de orden de compra por ID de producto detalle " + productoDetalleId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // El método DELETE /orden_compra_detalle/{id} es HEREDADO y sobreescrito para un soft delete si es necesario.
    // Si la lógica de eliminación es la misma que BaseController, puedes omitir este @Override.
    // Si tienes un manejo de errores o lógica específica para la eliminación de detalles, mantenlo.
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            ordenCompraDetalleService.eliminar(id); // Asume que este es tu soft delete
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            System.err.println("Error al eliminar lógicamente detalle de orden de compra ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}