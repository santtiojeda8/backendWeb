package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.ecommerce.ecommerce.Services.ProductoService;
import com.ecommerce.ecommerce.dto.ProductFilters;
import com.ecommerce.ecommerce.dto.ProductoDTO;
import com.ecommerce.ecommerce.dto.ProductoRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen (ajustar en producción si es necesario)
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }


    @GetMapping("/promociones") // Obtiene todos los productos activos con descuento/promoción
    public ResponseEntity<List<ProductoDTO>> obtenerProductosDTOConDescuento() {
        try {
            List<ProductoDTO> productosDTO = productoService.obtenerProductosPromocionalesDTO();
            return ResponseEntity.ok(productosDTO); // 200 OK
        } catch (Exception e) {
            // Loguear el error para depuración
            System.err.println("Error en el controlador al obtener productos promocionales DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }

    @GetMapping("") // Obtiene todos los productos activos
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductosDTO() {
        try {
            List<ProductoDTO> productosDTO = productoService.obtenerTodosLosProductosDTO();
            return ResponseEntity.ok(productosDTO); // 200 OK
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener todos los productos DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}") // Obtiene un producto activo por ID
    public ResponseEntity<ProductoDTO> obtenerProductoDTOPorId(@PathVariable Long id) {
        try {
            ProductoDTO productoDTO = productoService.obtenerProductoDTOPorId(id);
            // El servicio ya lanza EntityNotFoundException si no lo encuentra o está inactivo
            return ResponseEntity.ok(productoDTO); // 200 OK
        } catch (EntityNotFoundException e) {
            // Captura específica para productos no encontrados o inactivos
            System.err.println("Producto no encontrado o inactivo (ID: " + id + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener Producto DTO por ID (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/buscar") // Busca productos por denominación (palabra clave)
    public ResponseEntity<List<ProductoDTO>> buscarProductosPorNombre(@RequestParam String keyword) {
        try {
            List<ProductoDTO> productosDTO = productoService.buscarProductosPorDenominacion(keyword);
            return ResponseEntity.ok(productosDTO); // 200 OK
        } catch (Exception e) {
            System.err.println("Error en el controlador al buscar productos por nombre: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/categorias") // Obtiene una lista de todas las categorías disponibles
    public ResponseEntity<List<String>> getAllAvailableCategories() {
        try {
            List<String> categorias = productoService.getAllAvailableCategories();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener categorías: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/colores") // Obtiene una lista de todos los colores disponibles
    public ResponseEntity<List<String>> getAllAvailableColors() {
        try {
            List<String> colores = productoService.getAllAvailableColors();
            return ResponseEntity.ok(colores);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener colores: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/talles") // Obtiene una lista de todos los talles disponibles
    public ResponseEntity<List<String>> getAllAvailableTalles() {
        try {
            List<String> talles = productoService.getAllAvailableTalles();
            return ResponseEntity.ok(talles);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener tallas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/filtrar") // Permite filtrar y ordenar productos usando un objeto ProductFilters en el cuerpo de la solicitud
    public ResponseEntity<List<ProductoDTO>> filtrarYOrdenarProductos(
            @RequestBody(required = false) ProductFilters filters // 'required = false' permite que los filtros sean opcionales
    ) {
        try {
            // Si el cuerpo de la solicitud es nulo, inicializa un ProductFilters vacío para evitar NullPointerExceptions
            ProductFilters actualFilters = filters != null ? filters : new ProductFilters();

            List<ProductoDTO> productosFiltrados = productoService.filtrarYOrdenarProductos(
                    actualFilters.getDenominacion(),
                    actualFilters.getCategorias(),
                    actualFilters.getSexo(),
                    actualFilters.getTienePromocion(),
                    actualFilters.getMinPrice(),
                    actualFilters.getMaxPrice(),
                    actualFilters.getColores(),
                    actualFilters.getTalles(),
                    actualFilters.getStockMinimo(),
                    actualFilters.getOrderBy(),
                    actualFilters.getOrderDirection()
            );
            return ResponseEntity.ok(productosFiltrados);
        } catch (Exception e) {
            System.err.println("Error en el controlador al filtrar productos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("") // Crea un nuevo producto (sin carga de imágenes en esta request)
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoRequestDTO requestDTO) {
        try {
            // El segundo parámetro 'null' indica que no hay MultipartFile para imágenes en esta solicitud.
            // Si necesitas cargar imágenes junto con la data del producto, se requeriría un enfoque @RequestPart.
            ProductoDTO nuevoProducto = productoService.crearProducto(requestDTO, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto); // 201 Created
        } catch (Exception e) {
            System.err.println("Error en el controlador al crear producto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request (ej. datos inválidos)
        }
    }

    @PutMapping("/{id}") // Actualiza un producto existente
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id, @RequestBody ProductoRequestDTO requestDTO) {
        try {
            // Similar a crearProducto, se pasa 'null' para las imágenes si no se manejan en esta request.
            ProductoDTO productoActualizado = productoService.actualizarProducto(id, requestDTO, null);
            return ResponseEntity.ok(productoActualizado); // 200 OK
        } catch (EntityNotFoundException e) {
            System.err.println("Producto no encontrado para actualizar (ID: " + id + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Error en el controlador al actualizar producto (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}") // Desactiva (soft delete) un producto
    public ResponseEntity<?> desactivarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProductoPorId(id); // Usa el método específico del servicio para soft delete
            return ResponseEntity.noContent().build(); // 204 No Content (indica que la operación fue exitosa sin contenido de retorno)
        } catch (EntityNotFoundException e) {
            System.err.println("Producto no encontrado para desactivar (ID: " + id + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        } catch (Exception e) {
            System.err.println("Error al desactivar producto desde el controlador (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @PutMapping("/activar/{id}") // Activa un producto (cambia 'activo' a true)
    public ResponseEntity<ProductoDTO> activarProducto(@PathVariable Long id) {
        try {
            ProductoDTO activatedProduct = productoService.activarProducto(id);
            return ResponseEntity.ok(activatedProduct); // 200 OK
        } catch (EntityNotFoundException e) {
            System.err.println("Producto no encontrado para activar (ID: " + id + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Error al activar producto desde el controlador (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}