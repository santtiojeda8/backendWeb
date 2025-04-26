package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/categoria/{categoriaId}")
    public List<Producto> findAllByCategoriaId(@PathVariable Long categoriaId) throws Exception {
        return productoService.findAllByCategoriaId(categoriaId);
    }

    @GetMapping("/promociones")
    public List<Producto> findProductosConPromocion() throws Exception {
        return productoService.findProductosConPromocion();
    }

    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam("keyword") String keyword) throws Exception {
        return productoService.buscarPorNombre(keyword);
    }
}
