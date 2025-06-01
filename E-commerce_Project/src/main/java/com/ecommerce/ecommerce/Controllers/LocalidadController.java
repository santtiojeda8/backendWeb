package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Services.LocalidadService;
import com.ecommerce.ecommerce.dto.LocalidadDTO; // ¡Importamos LocalidadDTO!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors; // Necesario para .stream().map().collect()

@RestController
@RequestMapping("/localidades")
public class LocalidadController extends BaseController<Localidad, Long> {
    private final LocalidadService localidadService;

    @Autowired
    public LocalidadController(LocalidadService localidadService) {
        super(localidadService);
        this.localidadService = localidadService;
    }

    // Modificado: Ahora devuelve ResponseEntity<List<LocalidadDTO>>
    @GetMapping("/por-provincia/{provinciaId}")
    public ResponseEntity<List<LocalidadDTO>> getLocalidadesByProvinciaId(@PathVariable Long provinciaId) {
        try {
            // Se obtienen las entidades, el mapeo se hace en el servicio
            List<Localidad> localidades = localidadService.findByProvinciaId(provinciaId);

            // Mapeamos las entidades a DTOs ANTES de devolverlas
            List<LocalidadDTO> localidadDTOS = localidades.stream()
                    .map(localidadService::mapToLocalidadDTO) // Llamamos al nuevo método de mapeo en el servicio
                    .collect(Collectors.toList());

            return ResponseEntity.ok(localidadDTOS);
        } catch (Exception e) {
            System.err.println("Error al obtener localidades para la provincia con ID " + provinciaId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}