package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Services.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orden_compra")
public class OrdenCompraController extends BaseController<OrdenCompra, Long> {

    private final OrdenCompraService ordenCompraService;

    @Autowired
    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        super(ordenCompraService);
        this.ordenCompraService = ordenCompraService;
    }

    @GetMapping("/fecha")
    public List<OrdenCompra> obtenerPorFecha(@RequestParam("fecha") String fecha) throws Exception {
        // Se recibe la fecha como String y se convierte a LocalDateTime
        LocalDateTime fechaCompra = LocalDateTime.parse(fecha);
        return ordenCompraService.obtenerPorFecha(fechaCompra);
    }

}
