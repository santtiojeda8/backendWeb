package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Services.ProductoDetalleService;
import com.ecommerce.ecommerce.dto.ProductoDetalleDTO; // ¡Usamos este para todo ahora!
// No se necesita ProductoDetalleRequestDTO si ProductoDetalleDTO maneja ambas responsabilidades

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/producto_detalle")
@CrossOrigin(origins = "*") // Puedes ajustar esto según tus necesidades de CORS
public class ProductoDetalleController extends BaseController<ProductoDetalle, Long> {

    private final ProductoDetalleService productoDetalleService;

    @Autowired
    public ProductoDetalleController(ProductoDetalleService productoDetalleService) {
        super(productoDetalleService); // El BaseController ya maneja operaciones de entidad
        this.productoDetalleService = productoDetalleService;
    }

    // --- Endpoints para CRUD con DTOs (ahora usando ProductoDetalleDTO para entrada y salida) ---

    // Método para crear un ProductoDetalle usando el DTO
    @PostMapping("/dto")
    public ResponseEntity<?> crearDesdeDTO(@RequestBody ProductoDetalleDTO dto) { // Recibe ProductoDetalleDTO
        try {
            ProductoDetalle newDetalleEntity = productoDetalleService.crearDesdeDTO(dto); // El servicio devuelve la entidad
            ProductoDetalleDTO responseDTO = new ProductoDetalleDTO(newDetalleEntity); // Mapea la entidad a DTO de respuesta
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            System.err.println("Error al crear ProductoDetalle desde DTO: " + e.getMessage());
            e.printStackTrace();
            // Para una mejor práctica, podrías usar un DTO de error o simplemente el mensaje
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof IllegalArgumentException) {
                status = HttpStatus.BAD_REQUEST; // 400 Bad Request para datos inválidos
            }
            return ResponseEntity.status(status).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Método para actualizar un ProductoDetalle usando el DTO
    @PutMapping("/dto/{id}")
    public ResponseEntity<?> actualizarDesdeDTO(@PathVariable Long id, @RequestBody ProductoDetalleDTO dto) { // Recibe ProductoDetalleDTO
        try {
            // No es necesario dto.setId(id); aquí, el ID se pasa por PathVariable
            ProductoDetalle updatedDetalleEntity = productoDetalleService.actualizarDesdeDTO(id, dto); // El servicio devuelve la entidad
            ProductoDetalleDTO responseDTO = new ProductoDetalleDTO(updatedDetalleEntity); // Mapea la entidad a DTO de respuesta
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            System.err.println("Error al actualizar ProductoDetalle desde DTO con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof jakarta.persistence.EntityNotFoundException) {
                status = HttpStatus.NOT_FOUND; // 404 Not Found si no se encuentra la entidad
            } else if (e instanceof IllegalArgumentException) {
                status = HttpStatus.BAD_REQUEST; // 400 Bad Request para datos inválidos
            }
            return ResponseEntity.status(status).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // --- Deshabilitar los endpoints de BaseController para operar con entidades (opcional, pero buena práctica) ---
    @Override
    @PostMapping("")
    public ResponseEntity<ProductoDetalle> crear(@RequestBody ProductoDetalle entity) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build(); // 405 Method Not Allowed
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDetalle> actualizar(@PathVariable Long id, @RequestBody ProductoDetalle entity) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build(); // 405 Method Not Allowed
    }

    // --- Métodos de consulta que devuelven DTOs ---

    // Listar todos los ProductoDetalle como DTOs
    @GetMapping("/dto")
    public ResponseEntity<?> listarDTO() {
        try {
            List<ProductoDetalle> entities = productoDetalleService.listar(); // Llama al listar() de BaseService (devuelve entidades)
            List<ProductoDetalleDTO> dtos = entities.stream()
                    .map(ProductoDetalleDTO::new) // Mapea cada entidad a un DTO
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("Error al listar Productodetalle (DTOs): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Buscar un ProductoDetalle por ID como DTO
    @GetMapping("/dto/{id}")
    public ResponseEntity<?> buscarPorIdDTO(@PathVariable Long id) {
        try {
            ProductoDetalle entity = productoDetalleService.buscarPorId(id); // Llama a buscarPorId() de BaseService (devuelve entidad)
            ProductoDetalleDTO dto = new ProductoDetalleDTO(entity); // Mapea la entidad a un DTO
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

    // Método para buscar ProductoDetalles por ID de Producto (ya devuelve DTOs desde el servicio)
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> findAllByProductoId(@PathVariable Long productoId) {
        try {
            List<ProductoDetalleDTO> detalles = productoDetalleService.findAllByProductoId(productoId);
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            System.err.println("Error al buscar ProductoDetalles por ID de Producto " + productoId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Método para buscar ProductoDetalle por atributos (ya devuelve DTO desde el servicio)
    @GetMapping("/buscar")
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
}