package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.enums.Color;
import com.ecommerce.ecommerce.Entities.enums.Talle;
import com.ecommerce.ecommerce.Services.ProductoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producto-detalle")
public class ProductoDetalleController extends BaseController<ProductoDetalle, Long> {

    private final ProductoDetalleService productoDetalleService;

    @Autowired
    public ProductoDetalleController(ProductoDetalleService productoDetalleService) {
        super(productoDetalleService);
        this.productoDetalleService = productoDetalleService;
    }

    @GetMapping("/producto/{productoId}")
    public List<ProductoDetalle> findAllByProductoId(@PathVariable Long productoId) throws Exception {
        return productoDetalleService.findAllByProductoId(productoId);
    }

    @GetMapping("/buscar")
    public ProductoDetalle findByProductoIdAndTalleAndColor(@RequestParam Long productoId,
                                                            @RequestParam Talle talle,
                                                            @RequestParam Color color) throws Exception {
        return productoDetalleService.findByProductoIdAndTalleAndColor(productoId, talle, color);
    }

    @GetMapping("/stock-mayor-a/{stockMinimo}")
    public List<ProductoDetalle> findAllByStockActualGreaterThan(@PathVariable Integer stockMinimo) throws Exception {
        return productoDetalleService.findAllByStockActualGreaterThan(stockMinimo);
    }

    @GetMapping("/filtrar")
    public List<ProductoDetalle> filtrarPorOpciones(@RequestParam(required = false) Long productoId,
                                                    @RequestParam(required = false) Color color,
                                                    @RequestParam(required = false) Talle talle,
                                                    @RequestParam(required = false) Integer stockMin) throws Exception {
        return productoDetalleService.filtrarPorOpciones(productoId, color, talle, stockMin);
    }

    @GetMapping("/talles/{productoId}")
    public List<Talle> obtenerTallesDisponibles(@PathVariable Long productoId) throws Exception {
        return productoDetalleService.obtenerTallesDisponibles(productoId);
    }

    @GetMapping("/colores/{productoId}")
    public List<Color> obtenerColoresDisponibles(@PathVariable Long productoId) throws Exception {
        return productoDetalleService.obtenerColoresDisponibles(productoId);
    }

    @PostMapping("/descontar-stock")
    public void descontarStock(@RequestParam Long productoDetalleId,
                               @RequestParam int cantidad) throws Exception {
        productoDetalleService.descontarStock(productoDetalleId, cantidad);
    }

    @GetMapping("/disponible")
    public boolean estaDisponible(@RequestParam Long productoId,
                                  @RequestParam Talle talle,
                                  @RequestParam Color color) throws Exception {
        return productoDetalleService.estaDisponible(productoId, talle, color);
    }
}
