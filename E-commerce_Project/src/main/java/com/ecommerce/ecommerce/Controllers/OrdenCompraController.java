package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Services.OrdenCompraService;
import com.ecommerce.ecommerce.dto.OrdenCompraDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // Asegúrate de que esto está importado
import java.util.List;
import java.util.stream.Collectors; // Se mantiene por si se usa en otros métodos no mostrados

@RestController
@RequestMapping("/orden_compra")
public class OrdenCompraController extends BaseController<OrdenCompra, Long> {

    private final OrdenCompraService ordenCompraService;

    @Autowired
    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        super(ordenCompraService); // Pasa el servicio al BaseController
        this.ordenCompraService = ordenCompraService;
    }

    // --- MÉTODOS ESPECÍFICOS PARA DTOs (con rutas únicas /dto) ---

    // Este @GetMapping("/dto") maneja la solicitud GET /orden_compra/dto
    // Permite obtener todas las órdenes de compra como DTOs, evitando conflicto con GET /orden_compra de BaseController.
    @GetMapping("/dto")
    public ResponseEntity<List<OrdenCompraDTO>> listarDTO() {
        try {
            List<OrdenCompraDTO> dtos = ordenCompraService.findAllDTO();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("Error al listar órdenes de compra DTOs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Este @GetMapping("/dto/{id}") maneja la solicitud GET /orden_compra/dto/{id}
    // Permite obtener una orden de compra por ID como DTO, evitando conflicto con GET /orden_compra/{id} de BaseController.
    @GetMapping("/dto/{id}")
    public ResponseEntity<OrdenCompraDTO> buscarPorIdDTO(@PathVariable Long id) {
        try {
            OrdenCompraDTO dto = ordenCompraService.findByIdDTO(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.err.println("Error al buscar orden de compra DTO por ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrada o inactiva")) { // Manejo específico para 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Este @PostMapping("/dto") maneja la solicitud POST /orden_compra/dto
    // Permite crear una orden de compra desde un DTO, evitando conflicto con POST /orden_compra de BaseController.
    @PostMapping("/dto")
    public ResponseEntity<OrdenCompraDTO> crearDTO(@RequestBody OrdenCompraDTO ordenCompraDTO) {
        try {
            OrdenCompra newEntity = ordenCompraService.saveOrdenCompraFromDTO(ordenCompraDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ordenCompraService.mapOrdenCompraToDTO(newEntity));
        } catch (Exception e) {
            System.err.println("Error al crear orden de compra desde DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Este @PutMapping("/dto/{id}") maneja la solicitud PUT /orden_compra/dto/{id}
    // Permite actualizar una orden de compra por ID desde un DTO, evitando conflicto con PUT /orden_compra de BaseController.
    @PutMapping("/dto/{id}")
    public ResponseEntity<OrdenCompraDTO> actualizarDTO(@PathVariable Long id, @RequestBody OrdenCompraDTO ordenCompraDTO) {
        try {
            OrdenCompra updatedEntity = ordenCompraService.updateOrdenCompraFromDTO(id, ordenCompraDTO);
            return ResponseEntity.ok(ordenCompraService.mapOrdenCompraToDTO(updatedEntity));
        } catch (Exception e) {
            System.err.println("Error al actualizar orden de compra ID " + id + " desde DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // --- Otros métodos específicos (eliminar, por usuario, por fecha) ---

    // Este @Override para @DeleteMapping("/{id}") SOBRESCRIBE el método 'eliminar' de BaseController.
    // Esto es NECESARIO si tu implementación de 'eliminar' aquí es diferente o quieres un manejo de error distinto.
    // Si la lógica es la misma que la de BaseController (soft delete), puedes incluso eliminar este @Override
    // y dejar que BaseController maneje el DELETE /orden_compra/{id}.
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            ordenCompraService.eliminar(id); // Asumo que este es tu soft delete
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            System.err.println("Error al eliminar lógicamente orden de compra ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Este @PutMapping("/activar/{id}") también podría sobreescribir un método de BaseController
    // si BaseController tuviera uno. Si BaseController no tiene `activar`, este es un endpoint específico.
    // Asegúrate de que el método 'activar' en tu servicio realmente activa la entidad.
    // @Override // Solo si BaseController tiene un método activar con esta firma y ruta
    // @PutMapping("/activar/{id}")
    // public ResponseEntity<OrdenCompra> activar(@PathVariable Long id) { // Si retorna entidad, no DTO
    //     try {
    //         OrdenCompra entity = ordenCompraService.activar(id);
    //         return ResponseEntity.ok(entity);
    //     } catch (Exception e) {
    //         // ...
    //     }
    // }


    // Este @GetMapping("/fecha") maneja la solicitud GET /orden_compra/fecha
    // Es un endpoint específico para filtrar por fecha.
    @GetMapping("/fecha")
    public ResponseEntity<List<OrdenCompraDTO>> obtenerPorFecha(@RequestParam("fecha") String fecha) {
        try {
            LocalDateTime fechaCompra = LocalDateTime.parse(fecha); // Importante: formato ISO 8601
            List<OrdenCompraDTO> dtos = ordenCompraService.obtenerPorFecha(fechaCompra);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("Error al obtener órdenes de compra por fecha " + fecha + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Este @GetMapping("/usuario/{userId}") maneja la solicitud GET /orden_compra/usuario/{userId}
    // Es un endpoint específico para obtener órdenes por usuario.
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<OrdenCompraDTO>> obtenerPorUsuarioDTO(@PathVariable Long userId) {
        try {
            List<OrdenCompraDTO> dtos = ordenCompraService.obtenerPorUsuarioDTO(userId);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("Error al obtener órdenes de compra por usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}