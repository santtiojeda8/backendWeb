package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Services.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/direcciones")
public class DireccionController extends BaseController<Direccion, Long> {

    private final DireccionService direccionService;

    @Autowired
    public DireccionController(DireccionService direccionService) {
        super(direccionService);
        this.direccionService = direccionService;
    }

    // Listar direcciones por ID de localidad
    @GetMapping("/localidad/{idLocalidad}")
    public ResponseEntity<List<Direccion>> listarPorLocalidad(@PathVariable Long idLocalidad) {
        try {
            List<Direccion> direcciones = direccionService.listarPorLocalidad(idLocalidad);
            return ResponseEntity.ok(direcciones);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // Listar direcciones por ID de cliente
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Direccion>> listarPorClientesID(@PathVariable Long idCliente) {
        try {
            List<Direccion> direcciones = direccionService.listarPorClientesID(idCliente);
            return ResponseEntity.ok(direcciones);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
