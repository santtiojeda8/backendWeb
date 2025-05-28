package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Services.LocalidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/localidades")
public class LocalidadController extends BaseController<Localidad, Long> {
    private final LocalidadService localidadService; // Mantenemos para métodos específicos como findByProvinciaId

    @Autowired
    public LocalidadController(LocalidadService localidadService) {
        super(localidadService); // Pasamos el servicio a la clase base
        this.localidadService = localidadService; // Opcional: para usar en métodos específicos de LocalidadController
    }


    // Obtener localidades por ID de provincia (Este sí es un endpoint específico y no redundante)
    // Coincide con GET /localidades/por-provincia/{provinciaId}
    @GetMapping("/por-provincia/{provinciaId}")
    public ResponseEntity<List<Localidad>> getLocalidadesByProvinciaId(@PathVariable Long provinciaId) {
        try {
            List<Localidad> localidades = localidadService.findByProvinciaId(provinciaId);
            return ResponseEntity.ok(localidades);
        } catch (Exception e) {
            System.err.println("Error al obtener localidades para la provincia con ID " + provinciaId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}