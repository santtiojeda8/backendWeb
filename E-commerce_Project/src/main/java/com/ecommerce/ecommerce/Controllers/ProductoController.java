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

    // 🔹 Buscar productos por palabra clave (nombre)
    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam("keyword") String keyword) throws Exception {
        return productoService.buscarPorNombre(keyword);
    }

    // 🔹 Obtener productos con promoción en formato DTO (precio final incluido)
    @GetMapping("/dto/promociones")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosDTOConDescuento() throws Exception {
        List<ProductoDTO> productosDTO = productoService.obtenerProductosConDTOConDescuento();
        return ResponseEntity.ok(productosDTO);
    }

    // 🔹 Obtener todos los productos en formato DTO (con o sin promoción)
    @GetMapping("/dto")
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductosDTO() throws Exception {
        List<ProductoDTO> productosDTO = productoService.obtenerProductosConDTOConDescuento();
        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoDTOPorId(@PathVariable Long id) throws Exception {
        return productoService.buscarPorId(id)
                .map(producto -> {
                    ProductoDTO dto = productoService.mapearProductoADTO(producto);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
