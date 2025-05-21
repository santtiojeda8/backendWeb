// src/main/java/com/ecommerce/ecommerce/Controllers/ProvinciaController.java
package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Provincia;
import com.ecommerce.ecommerce.Services.ProvinciaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/provincias")
@AllArgsConstructor
public class ProvinciaController {

    private final ProvinciaService provinciaService;

    @GetMapping
    public ResponseEntity<List<Provincia>> getAllProvincias() {
        try {
            // CAMBIO AQUÍ: Llamar a listar() en lugar de findAll()
            List<Provincia> provincias = provinciaService.listar();
            return ResponseEntity.ok(provincias);
        } catch (Exception e) {
            // Es buena práctica loggear el error para debugging
            System.err.println("Error al obtener todas las provincias: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build(); // Manejo de errores simple
        }
    }
}