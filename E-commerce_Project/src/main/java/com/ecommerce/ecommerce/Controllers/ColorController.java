package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Color;
import com.ecommerce.ecommerce.Services.ColorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/colores") // Endpoint base para los colores
public class ColorController extends BaseController<Color, Long> {

    private final ColorService colorService;

    public ColorController(ColorService colorService) {
        super(colorService);
        this.colorService = colorService;
    }

    // Endpoint específico para buscar un color por su nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Color> buscarPorNombre(@PathVariable String nombre) {
        try {
            Color color = colorService.buscarPorNombre(nombre);
            return ResponseEntity.ok(color);
        } catch (Exception e) {
            System.err.println("Error al buscar color por nombre " + nombre + ": " + e.getMessage());
            if (e.getMessage().contains("no encontrado o inactivo")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Los métodos CRUD (listar, buscarPorId, crear, actualizar, eliminar, activar)
    // se heredan automáticamente de BaseController
}
