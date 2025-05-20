package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Entities.enums.Sexo; // Asegúrate de importar Sexo si lo usas en ProductFilters
import com.ecommerce.ecommerce.Services.ProductoService;
// Importar la clase ProductFilters
import com.ecommerce.ecommerce.dto.ProductFilters;
// Importar ProductoDTO
import com.ecommerce.ecommerce.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos") // Ruta base para los endpoints de productos
public class ProductoController extends BaseController<Producto, Long> {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        super(productoService);
        this.productoService = productoService;
    }

    // --- Endpoints que devuelven DTOs ---

    // Obtener productos con promoción en formato DTO (precio final incluido)
    @GetMapping("/dto/promociones")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosDTOConDescuento() { // Eliminado throws Exception aquí para manejo interno
        try {
            List<ProductoDTO> productosDTO = productoService.obtenerProductosPromocionalesDTO();
            return ResponseEntity.ok(productosDTO);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener productos promocionales DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Devuelve un error 500
        }
    }

    // Obtener todos los productos en formato DTO (con o sin promoción)
    @GetMapping("/dto")
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductosDTO() { // Eliminado throws Exception aquí
        try {
            List<ProductoDTO> productosDTO = productoService.obtenerTodosLosProductosDTO();
            return ResponseEntity.ok(productosDTO);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener todos los productos DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoDTOPorId(@PathVariable Long id) { // Eliminado throws Exception aquí
        try {
            ProductoDTO productoDTO = productoService.obtenerProductoDTOPorId(id);
            // Manejar caso not found si el servicio lanza una excepción específica o devuelve null
            if (productoDTO == null) {
                return ResponseEntity.notFound().build(); // Devuelve 404 si el producto no se encuentra
            }
            return ResponseEntity.ok(productoDTO);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener Producto DTO por ID: " + e.getMessage());
            e.printStackTrace();
            // Puedes refinar esto para devolver 404 si la excepción indica "no encontrado"
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoints para obtener listas de filtros disponibles (como Strings) ---

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

    // --- Endpoint actualizado para filtrar y ordenar productos ---
    // Recibe los parámetros de filtro y ordenamiento en el body de la solicitud POST.
    @PostMapping("/filtrar")
    public ResponseEntity<List<ProductoDTO>> filtrarYOrdenarProductos(
            @RequestBody(required = false) ProductFilters filters // Recibe el objeto de filtro completo en el body
    ) {
        try {
            // Asegurarse de que el objeto filters no sea null si required = false
            ProductFilters actualFilters = filters != null ? filters : new ProductFilters();

            // Llama al método de filtrar y ordenar en el servicio, pasando los campos del objeto filters
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
            // Manejo de errores
            System.err.println("Error en el controlador al filtrar productos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Otros endpoints (CRUD) si los tienes definidos en BaseController y los necesitas ---
    // Si tus métodos de BaseController ya trabajan con la Entidad Producto, puedes heredarlos directamente.
    // Si necesitas que trabajen con DTOs de entrada/salida, deberás sobreescribirlos aquí
    // y añadir la lógica de mapeo DTO <-> Entidad.

    /*
    // Ejemplo de cómo sobreescribir un método si necesitas trabajar con DTOs de entrada
    @Override // Si estás sobreescribiendo un método de BaseController
    @PostMapping // O el método HTTP correcto (PUT, DELETE, etc.)
    public ResponseEntity<ProductoDTO> create(@RequestBody ProductoDTO productoDTO) {
        try {
            // Necesitas un método para mapear de ProductoDTO a Producto (Entidad)
            Producto entidadACrear = mapearDTOaEntidad(productoDTO); // Implementa este método

            // Llama al método de creación del servicio (que trabaja con la Entidad)
            Producto entidadCreada = productoService.create(entidadACrear); // Asumiendo un método create en el servicio/baseService

            // Mapea la entidad creada de vuelta a DTO para la respuesta
            ProductoDTO dtoCreado = mapearProductoADTO(entidadCreada); // Reutiliza el mapeo Entidad -> DTO

            return ResponseEntity.status(HttpStatus.CREATED).body(dtoCreado);

        } catch (Exception e) {
            System.err.println("Error en el controlador al crear producto (desde DTO): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Método para mapear de ProductoDTO a Producto (Entidad) - NECESITA IMPLEMENTACIÓN REAL
    private Producto mapearDTOaEntidad(ProductoDTO productoDTO) {
        // Implementa la lógica de mapeo de DTO a Entidad aquí.
        // Esto implica crear una nueva instancia de Producto,
        // copiar los datos del DTO a la Entidad,
        // y manejar las relaciones (buscar Categorias, Imagenes, ProductoDetalles existentes o crear nuevos).
        // Esto puede ser complejo dependiendo de tu modelo de datos y lógica de negocio.
        return null; // Implementar el mapeo real
    }
    */
}
