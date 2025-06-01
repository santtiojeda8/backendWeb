package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Services.DireccionService;
import com.ecommerce.ecommerce.dto.DireccionDTO; // ¡Importamos DireccionDTO!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors; // Necesario para .stream().map().collect()

@RestController
@RequestMapping("/direcciones")
public class DireccionController extends BaseController<Direccion, Long> {

    private final DireccionService direccionService;

    @Autowired
    public DireccionController(DireccionService direccionService) {
        super(direccionService);
        this.direccionService = direccionService;
    }

    // Modificado: Ahora devuelve ResponseEntity<List<DireccionDTO>>
    @GetMapping("/localidad/{idLocalidad}")
    public ResponseEntity<List<DireccionDTO>> listarPorLocalidad(@PathVariable Long idLocalidad) {
        try {
            List<Direccion> direcciones = direccionService.listarPorLocalidad(idLocalidad);
            // Mapeamos las entidades a DTOs ANTES de devolverlas
            List<DireccionDTO> direccionDTOS = direcciones.stream()
                    .map(direccionService::mapToDireccionDTO) // Llamamos al nuevo método de mapeo en el servicio
                    .collect(Collectors.toList());
            return ResponseEntity.ok(direccionDTOS);
        } catch (Exception e) {
            System.err.println("Error al listar direcciones por localidad: " + e.getMessage());
            e.printStackTrace(); // Es bueno tener un stack trace para depuración
            return ResponseEntity.status(404).body(null);
        }
    }

    // Modificado: Ahora devuelve ResponseEntity<List<DireccionDTO>>
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<DireccionDTO>> listarPorClientesID(@PathVariable Long idCliente) {
        try {
            List<Direccion> direcciones = direccionService.listarPorClientesID(idCliente);
            // Mapeamos las entidades a DTOs ANTES de devolverlas
            List<DireccionDTO> direccionDTOS = direcciones.stream()
                    .map(direccionService::mapToDireccionDTO) // Llamamos al nuevo método de mapeo en el servicio
                    .collect(Collectors.toList());
            return ResponseEntity.ok(direccionDTOS);
        } catch (Exception e) {
            System.err.println("Error al listar direcciones por cliente: " + e.getMessage());
            e.printStackTrace(); // Es bueno tener un stack trace para depuración
            return ResponseEntity.status(404).body(null);
        }
    }
}