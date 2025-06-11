package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Services.ProvinciaService; // Usamos el servicio modificado
import com.ecommerce.ecommerce.dto.ProvinciaDTO; // ¡Importamos el DTO!
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provincias") // Mantenemos la URL base /provincias
@AllArgsConstructor // Genera el constructor para la inyección de dependencia
public class ProvinciaController {

    private final ProvinciaService provinciaService; // Inyectamos el servicio

    // --- Métodos que ahora devuelven DTOs ---

    @GetMapping // GET /provincias
    public ResponseEntity<List<ProvinciaDTO>> getAllProvinciasDTO() { // Nombre del método aclaratorio
        try {
            List<ProvinciaDTO> provincias = provinciaService.findAllDTO(); // Llama al método del servicio que devuelve DTOs
            return ResponseEntity.ok(provincias);
        } catch (Exception e) {
            System.err.println("Error al obtener todas las provincias DTOs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}") // GET /provincias/{id}
    public ResponseEntity<ProvinciaDTO> getProvinciaByIdDTO(@PathVariable Long id) { // Nombre del método aclaratorio
        try {
            ProvinciaDTO provincia = provinciaService.findByIdDTO(id); // Llama al método del servicio que devuelve un DTO
            return ResponseEntity.ok(provincia);
        } catch (Exception e) {
            System.err.println("Error al buscar provincia DTO por ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrada o inactiva")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping // POST /provincias
    public ResponseEntity<ProvinciaDTO> createProvinciaFromDTO(@RequestBody ProvinciaDTO provinciaDTO) {
        try {
            ProvinciaDTO newProvincia = provinciaService.saveFromDTO(provinciaDTO); // Guarda desde DTO y devuelve DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(newProvincia);
        } catch (Exception e) {
            System.err.println("Error al crear provincia desde DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}") // PUT /provincias/{id}
    public ResponseEntity<ProvinciaDTO> updateProvinciaFromDTO(@PathVariable Long id, @RequestBody ProvinciaDTO provinciaDTO) {
        try {
            ProvinciaDTO updatedProvincia = provinciaService.updateFromDTO(id, provinciaDTO); // Actualiza desde DTO y devuelve DTO
            return ResponseEntity.ok(updatedProvincia);
        } catch (Exception e) {
            System.err.println("Error al actualizar provincia ID " + id + " desde DTO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}") // DELETE /provincias/{id}
    public ResponseEntity<?> deleteProvincia(@PathVariable Long id) {
        try {
            provinciaService.delete(id); // Llama al soft delete
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            System.err.println("Error al eliminar lógicamente provincia ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/activar/{id}") // PUT /provincias/activar/{id}
    public ResponseEntity<ProvinciaDTO> activateProvincia(@PathVariable Long id) {
        try {
            ProvinciaDTO activatedProvincia = provinciaService.activateFromService(id);
            return ResponseEntity.ok(activatedProvincia);
        } catch (Exception e) {
            System.err.println("Error al activar provincia ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}