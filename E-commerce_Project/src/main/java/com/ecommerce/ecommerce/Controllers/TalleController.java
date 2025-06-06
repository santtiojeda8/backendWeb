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
        super(talleService);
        this.talleService = talleService;
    }

    // Endpoint específico para buscar un talle por su nombre
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

}
