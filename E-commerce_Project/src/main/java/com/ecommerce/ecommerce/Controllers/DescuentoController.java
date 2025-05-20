package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Descuento;
import com.ecommerce.ecommerce.Services.DescuentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/descuentos")

public class DescuentoController extends BaseController<Descuento, Long> {

    private final DescuentosService descuentosService;

    @Autowired
    public DescuentoController(DescuentosService descuentosService) {
        super(descuentosService);
        this.descuentosService = descuentosService;
    }

    // Buscar descuento por ID de descuento
    @GetMapping("/{idDescuento}")
    public ResponseEntity<Descuento> obtenerPorIdDescuento(@PathVariable Long idDescuento) {
        try {
            Optional<Descuento> descuento = descuentosService.obtenerPorIdDescuento(idDescuento);
            return descuento.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
