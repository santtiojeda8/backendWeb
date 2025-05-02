package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Services.OrdenCompraDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orden_compra_detalle")
public class OrdenCompraDetalleController extends BaseController<OrdenCompraDetalle, Long> {

    private final OrdenCompraDetalleService ordenCompraDetalleService;

    @Autowired
    public OrdenCompraDetalleController(OrdenCompraDetalleService ordenCompraDetalleService) {
        super(ordenCompraDetalleService);
        this.ordenCompraDetalleService = ordenCompraDetalleService;
    }

    @GetMapping("/ordenCompra/{idOrdenCompra}")
    public List<OrdenCompraDetalle> obtenerPorOrdenCompra(@PathVariable Long idOrdenCompra) throws Exception {
        return ordenCompraDetalleService.obtenerPorOrdenCompra(idOrdenCompra);
    }

    @GetMapping("/productoDetalle/{idProductoDetalle}")
    public List<OrdenCompraDetalle> obtenerPorProductoDetalle(@PathVariable Long idProductoDetalle) throws Exception {
        return ordenCompraDetalleService.obtenerPorProductoDetalle(idProductoDetalle);
    }
}

