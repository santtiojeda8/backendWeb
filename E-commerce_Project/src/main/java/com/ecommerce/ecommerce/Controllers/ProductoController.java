package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.ecommerce.ecommerce.Services.ProductoService;
import com.ecommerce.ecommerce.dto.ProductFilters;
import com.ecommerce.ecommerce.dto.ProductoDTO;
import com.ecommerce.ecommerce.dto.ProductoRequestDTO; // ¡Importa este DTO de solicitud!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen. Ajusta según tus necesidades de seguridad.
public class ProductoController extends BaseController<Producto, Long> {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        super(productoService); // Pasa el ProductoService al constructor del BaseController
        this.productoService = productoService;
    }

    // --- Endpoints que devuelven DTOs (EXISTENTES - NO CAMBIAN) ---

    @GetMapping("/dto/promociones")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosDTOConDescuento() {
        try {
            List<ProductoDTO> productosDTO = productoService.obtenerProductosPromocionalesDTO();
            return ResponseEntity.ok(productosDTO);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener productos promocionales DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/dto") // Endpoint para obtener todos los productos como DTOs
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductosDTO() {
        try {
            List<ProductoDTO> productosDTO = productoService.obtenerTodosLosProductosDTO();
            return ResponseEntity.ok(productosDTO);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener todos los productos DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/dto/{id}") // Endpoint para obtener un producto por ID como DTO
    public ResponseEntity<ProductoDTO> obtenerProductoDTOPorId(@PathVariable Long id) {
        try {
            ProductoDTO productoDTO = productoService.obtenerProductoDTOPorId(id);
            if (productoDTO == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(productoDTO);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener Producto DTO por ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoints para obtener listas de filtros disponibles (como Strings - NO CAMBIAN) ---

    @GetMapping("/categorias")
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

    @GetMapping("/colores")
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

    @GetMapping("/talles")
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

    // --- Endpoint para filtrar y ordenar productos (NO CAMBIA) ---
    @PostMapping("/filtrar")
    public ResponseEntity<List<ProductoDTO>> filtrarYOrdenarProductos(
            @RequestBody(required = false) ProductFilters filters
    ) {
        try {
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

    // --- Métodos CRUD USANDO DTOs de Solicitud (ProductoRequestDTO) ---

    @PostMapping("/dto") // Endpoint para crear un producto
    public ResponseEntity<ProductoDTO> crearProductoDTO(@RequestBody ProductoRequestDTO requestDTO) {
        try {
            ProductoDTO nuevoProducto = productoService.crearProductoDesdeRequestDTO(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto); // 201 Created
        } catch (Exception e) {
            System.err.println("Error en el controlador al crear producto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/dto/{id}") // Endpoint para actualizar un producto
    public ResponseEntity<ProductoDTO> actualizarProductoDTO(@PathVariable Long id, @RequestBody ProductoRequestDTO requestDTO) {
        try {
            ProductoDTO productoActualizado = productoService.actualizarProductoDesdeRequestDTO(id, requestDTO);
            return ResponseEntity.ok(productoActualizado); // 200 OK
        } catch (Exception e) {
            System.err.println("Error en el controlador al actualizar producto (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("Producto no encontrado")) {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }

    @DeleteMapping("/eliminar/{id}") // Método para realizar un soft delete (cambiar el estado activo a falso)
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProductoPorId(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            System.err.println("Error al eliminar producto desde el controlador (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}