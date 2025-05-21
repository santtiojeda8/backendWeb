// src/main/java/com/ecommerce/ecommerce/Controllers/LocalidadController.java
package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Services.LocalidadService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// CAMBIO AQUÍ: Consistencia con la ruta en SecurityConfig
@RequestMapping("/localidades")
@AllArgsConstructor
public class LocalidadController {

    private final LocalidadService localidadService;

    @GetMapping
    public ResponseEntity<List<Localidad>> getAllLocalidades() {
        try {
            // CAMBIO AQUÍ: Llamar a listar() en lugar de findAll()
            List<Localidad> localidades = localidadService.listar();
            return ResponseEntity.ok(localidades);
        } catch (Exception e) {
            System.err.println("Error al obtener todas las localidades: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build(); // Manejo de errores simple
        }
    }

    // Obtener localidades por ID de provincia
    @GetMapping("/por-provincia/{provinciaId}")
    public ResponseEntity<List<Localidad>> getLocalidadesByProvinciaId(@PathVariable Long provinciaId) {
        try {
            // Este método ya estaba bien, ya que el servicio sí tiene findByProvinciaId
            List<Localidad> localidades = localidadService.findByProvinciaId(provinciaId);
            return ResponseEntity.ok(localidades);
        } catch (Exception e) {
            System.err.println("Error al obtener localidades para la provincia con ID " + provinciaId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build(); // Manejo de errores simple
        }
    }
}