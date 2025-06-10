package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Services.ProductoDetalleService;
import com.ecommerce.ecommerce.dto.ProductoDetalleDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/producto_detalle")
@CrossOrigin(origins = "*") // Ajusta según tus necesidades de CORS
public class ProductoDetalleController extends BaseController<ProductoDetalle, Long> {

    private final ProductoDetalleService productoDetalleService;

    @Autowired
    public ProductoDetalleController(ProductoDetalleService productoDetalleService) {
        super(productoDetalleService); // El BaseController ya maneja operaciones de entidad
        this.productoDetalleService = productoDetalleService;
    }

    // --- Endpoints para CRUD con DTOs (para el CLIENTE o uso general) ---

    // Método para crear un ProductoDetalle usando el DTO
    @PostMapping("/dto")
    public ResponseEntity<?> crearDesdeDTO(@RequestBody ProductoDetalleDTO dto) {
        try {
            ProductoDetalle newDetalleEntity = productoDetalleService.crearDesdeDTO(dto);
            ProductoDetalleDTO responseDTO = new ProductoDetalleDTO(newDetalleEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            System.err.println("Error al crear ProductoDetalle desde DTO: " + e.getMessage());
            e.printStackTrace();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof IllegalArgumentException) {
                status = HttpStatus.BAD_REQUEST;
            }
            return ResponseEntity.status(status).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Método para actualizar un ProductoDetalle usando el DTO
    @PutMapping("/dto/{id}")
    public ResponseEntity<?> actualizarDesdeDTO(@PathVariable Long id, @RequestBody ProductoDetalleDTO dto) {
        try {
            ProductoDetalle updatedDetalleEntity = productoDetalleService.actualizarDesdeDTO(id, dto);
            ProductoDetalleDTO responseDTO = new ProductoDetalleDTO(updatedDetalleEntity);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            System.err.println("Error al actualizar ProductoDetalle desde DTO con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof jakarta.persistence.EntityNotFoundException) {
                status = HttpStatus.NOT_FOUND;
            } else if (e instanceof IllegalArgumentException) {
                status = HttpStatus.BAD_REQUEST;
            }
            return ResponseEntity.status(status).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobreescribir los métodos del BaseController para forzar el uso de DTOs
    @Override
    @PostMapping("")
    public ResponseEntity<ProductoDetalle> crear(@RequestBody ProductoDetalle entity) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDetalle> actualizar(@PathVariable Long id, @RequestBody ProductoDetalle entity) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    // --- Métodos de consulta que devuelven DTOs para el CLIENTE ---

    // Listar todos los ProductoDetalle activos como DTOs (usa el listar() de BaseService que filtra por activo)
    @GetMapping("/dto") // Este es para el CLIENTE o vistas que solo muestran activos
    public ResponseEntity<?> listarDTO() {
        try {
            List<ProductoDetalle> entities = productoDetalleService.listar(); // Llama al listar() de BaseService (devuelve entidades activas)
            List<ProductoDetalleDTO> dtos = entities.stream()
                    .map(ProductoDetalleDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("Error al listar Productodetalle (DTOs): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Buscar un ProductoDetalle activo por ID como DTO (usa buscarPorId() de BaseService que filtra por activo)
    @GetMapping("/dto/{id}") // Este es para el CLIENTE
    public ResponseEntity<?> buscarPorIdDTO(@PathVariable Long id) {
        try {
            ProductoDetalle entity = productoDetalleService.buscarPorId(id); // Llama a buscarPorId() de BaseService (devuelve entidad activa)
            ProductoDetalleDTO dto = new ProductoDetalleDTO(entity);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.err.println("Error al buscar Productodetalle por ID (DTO) " + id + ": " + e.getMessage());
            e.printStackTrace();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof jakarta.persistence.EntityNotFoundException) {
                status = HttpStatus.NOT_FOUND;
            }
            return ResponseEntity.status(status).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Método para buscar ProductoDetalles por ID de Producto (ya devuelve DTOs desde el servicio, y filtra por activo)
    @GetMapping("/producto/{productoId}") // Este es para el CLIENTE
    public ResponseEntity<?> findAllByProductoId(@PathVariable Long productoId) {
        try {
            List<ProductoDetalleDTO> detalles = productoDetalleService.findAllByProductoId(productoId); // Llama al método que filtra por activo
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            System.err.println("Error al buscar ProductoDetalles por ID de Producto " + productoId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Método para buscar ProductoDetalle por atributos (ya devuelve DTO desde el servicio, y filtra por activo)
    @GetMapping("/buscar") // Este es para el CLIENTE
    public ResponseEntity<?> buscarProductoDetallePorAtributos(
            @RequestParam("productoId") Long productoId,
            @RequestParam("talleNombre") String talleNombre,
            @RequestParam("colorNombre") String colorNombre) {
        try {
            ProductoDetalleDTO detalle = productoDetalleService.findByProductoIdAndTalleAndColor(
                    productoId, talleNombre, colorNombre
            );
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            System.err.println("Error al buscar ProductoDetalle por atributos: " + e.getMessage());
            e.printStackTrace();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof jakarta.persistence.EntityNotFoundException) {
                status = HttpStatus.NOT_FOUND;
            }
            return ResponseEntity.status(status).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // --- NUEVOS ENDPOINTS PARA LA ADMINISTRACIÓN (NO FILTRAN POR ACTIVO POR DEFECTO) ---

    /**
     * Endpoint para la administración: Recupera TODOS los detalles de un producto dado,
     * incluyendo los activos e inactivos.
     * Usado para la vista de administración donde se gestionan todos los estados.
     */
    @GetMapping("/admin/producto/{productoId}")
    public ResponseEntity<?> findAllByProductoIdForAdmin(@PathVariable Long productoId) {
        try {
            // Llama al método específico del servicio que no filtra por 'activo'
            List<ProductoDetalleDTO> detalles = productoDetalleService.findAllByProductoIdForAdmin(productoId);
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            System.err.println("Error al buscar ProductoDetalles (ADMIN) por ID de Producto " + productoId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Endpoint para la administración: Desactiva lógicamente un ProductoDetalle (soft delete).
     * @param id El ID del ProductoDetalle a desactivar.
     */
    @PutMapping("/admin/deactivate/{id}")
    public ResponseEntity<?> deactivateProductDetail(@PathVariable Long id) {
        try {
            ProductoDetalleDTO updatedDetail = productoDetalleService.deactivate(id);
            return ResponseEntity.ok(updatedDetail);
        } catch (Exception e) {
            System.err.println("Error al desactivar ProductoDetalle con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof jakarta.persistence.EntityNotFoundException) {
                status = HttpStatus.NOT_FOUND;
            }
            return ResponseEntity.status(status).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Endpoint para la administración: Activa un ProductoDetalle previamente desactivado.
     * @param id El ID del ProductoDetalle a activar.
     */
    @PutMapping("/admin/activate/{id}")
    public ResponseEntity<?> activateProductDetail(@PathVariable Long id) {
        try {
            ProductoDetalleDTO updatedDetail = productoDetalleService.activate(id);
            return ResponseEntity.ok(updatedDetail);
        } catch (Exception e) {
            System.err.println("Error al activar ProductoDetalle con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof jakarta.persistence.EntityNotFoundException) {
                status = HttpStatus.NOT_FOUND;
            }
            return ResponseEntity.status(status).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}