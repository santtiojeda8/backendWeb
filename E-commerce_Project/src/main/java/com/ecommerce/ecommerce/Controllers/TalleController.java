package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Talle;
import com.ecommerce.ecommerce.Services.TalleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talles") // Endpoint base para los talles
public class TalleController extends BaseController<Talle, Long> {

    private final TalleService talleService;

    public TalleController(TalleService talleService) {
        super(talleService); // Pasa el servicio específico al constructor de BaseController
        this.talleService = talleService;
    }

    // Endpoint específico para buscar un talle por su nombre
    // Puedes mantenerlo aquí si es específico de Talle, o moverlo a BaseController si es genérico.
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Talle> buscarPorNombre(@PathVariable String nombre) {
        try {
            Talle talle = talleService.buscarPorNombre(nombre);
            return ResponseEntity.ok(talle);
        } catch (Exception e) {
            System.err.println("Error al buscar talle por nombre " + nombre + ": " + e.getMessage());
            if (e.getMessage().contains("no encontrado o inactivo")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // No necesitas implementar los métodos de CRUD o toggle aquí, ya se heredan de BaseController.
    // Solo si necesitas una lógica específica para Talle que difiera de la base.
}