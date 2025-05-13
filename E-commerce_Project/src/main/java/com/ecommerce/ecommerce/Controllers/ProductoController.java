package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Services.ProductoService;
import com.ecommerce.ecommerce.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController extends BaseController<Producto, Long> {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        super(productoService);
        this.productoService = productoService;
    }

    // Buscar productos por palabra clave (nombre)
    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam("keyword") String keyword) throws Exception {
        return productoService.buscarPorNombre(keyword);
    }

    // Obtener productos con promoción en formato DTO (precio final incluido)
    // Este endpoint ahora llama al método renombrado en el servicio
    @GetMapping("/dto/promociones")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosDTOConDescuento() throws Exception {
        List<ProductoDTO> productosDTO = productoService.obtenerProductosPromocionalesDTO(); // <--- Llama al método correcto para promocionales
        return ResponseEntity.ok(productosDTO);
    }

    // --- ENDPOINT CORREGIDO ---
    // Obtener todos los productos en formato DTO (con o sin promoción)
    // Este endpoint ahora llama al nuevo método en el servicio que trae TODOS los productos DTO
    @GetMapping("/dto")
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductosDTO() throws Exception {
        List<ProductoDTO> productosDTO = productoService.obtenerTodosLosProductosDTO(); // <--- ¡Llama al nuevo método!
        return ResponseEntity.ok(productosDTO);
    }

    // Obtener un producto específico en formato DTO por su ID
    // Este endpoint llama al nuevo método en el servicio para obtener un DTO por ID
    @GetMapping("/dto/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoDTOPorId(@PathVariable Long id) throws Exception {
        // Usamos el nuevo método en el servicio que ya devuelve un ProductoDTO o lanza excepción
        ProductoDTO productoDTO = productoService.obtenerProductoDTOPorId(id);
        return ResponseEntity.ok(productoDTO);

        // Alternativa si prefieres manejar el notFound aquí (menos común con servicios que lanzan excepciones)
        /*
        try {
             ProductoDTO productoDTO = productoService.obtenerProductoDTOPorId(id);
             return ResponseEntity.ok(productoDTO);
        } catch (Exception e) {
             // Si el servicio lanza una excepción porque no encontró el producto
             return ResponseEntity.notFound().build();
        }
        */
    }

}
