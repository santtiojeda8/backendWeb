package com.ecommerce.ecommerce.Controllers;

// No importes la entidad Descuento si solo vas a trabajar con DTOs en el controlador
// import com.ecommerce.ecommerce.Entities.Descuento;

import com.ecommerce.ecommerce.Services.DescuentosService;
import com.ecommerce.ecommerce.dto.DescuentoDTO; // ¡Importa tu DTO!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Para los códigos de estado HTTP
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// No necesitas Optional aquí si el servicio ya lanza excepciones para "no encontrado"
// import java.util.Optional;

@RestController
@RequestMapping("/descuentos")
// ¡IMPORTANTE! NO EXTENDER BASECONTROLLER para este controlador si quieres que maneje DTOs explícitamente.
// public class DescuentoController extends BaseController<Descuento, Long> {
public class DescuentoController {

    private final DescuentosService descuentosService;

    @Autowired
    public DescuentoController(DescuentosService descuentosService) {
        // Si no extiendes BaseController, no llamas a super()
        // super(descuentosService);
        this.descuentosService = descuentosService;
    }

    // Endpoint para obtener TODOS los descuentos (LISTAR)
    // Este endpoint es similar al `getAll` de un BaseController, pero devuelve DTOs.
    @GetMapping
    public ResponseEntity<List<DescuentoDTO>> getAllDescuentos() {
        try {
            // Llama al nuevo método del servicio que devuelve List<DescuentoDTO>
            List<DescuentoDTO> descuentos = descuentosService.listarDTOs();
            return ResponseEntity.ok(descuentos);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener todos los descuentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para obtener un descuento por ID
    @GetMapping("/{id}") // Usamos {id} por convención, en lugar de {idDescuento}
    public ResponseEntity<DescuentoDTO> getDescuentoById(@PathVariable Long id) { // Cambiado el nombre de la variable
        try {
            // Llama al nuevo método del servicio que devuelve un DescuentoDTO
            DescuentoDTO descuento = descuentosService.obtenerDescuentoDTOPorId(id);
            return ResponseEntity.ok(descuento);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener descuento por ID: " + e.getMessage());
            // Si el servicio ya lanza una excepción para "no encontrado", un 404 es apropiado.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint para CREAR un nuevo descuento
    @PostMapping
    public ResponseEntity<DescuentoDTO> createDescuento(@RequestBody DescuentoDTO dto) {
        try {
            DescuentoDTO savedDto = descuentosService.crearDescuento(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDto); // Código 201 Created
        } catch (Exception e) {
            System.err.println("Error en el controlador al crear descuento: " + e.getMessage());
            // Un BAD_REQUEST (400) es apropiado para errores de validación o datos inválidos
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Endpoint para ACTUALIZAR un descuento existente
    @PutMapping("/{id}")
    public ResponseEntity<DescuentoDTO> updateDescuento(@PathVariable Long id, @RequestBody DescuentoDTO dto) {
        try {
            DescuentoDTO updatedDto = descuentosService.actualizarDescuento(id, dto);
            return ResponseEntity.ok(updatedDto); // Código 200 OK
        } catch (Exception e) {
            System.err.println("Error en el controlador al actualizar descuento: " + e.getMessage());
            // BAD_REQUEST (400) si los datos son inválidos, NOT_FOUND (404) si el ID no existe
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Endpoint para "ELIMINAR" (inactivar) un descuento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDescuento(@PathVariable Long id) {
        try {
            // Llama al método de soft delete del servicio (que a su vez llama a BaseService.eliminar)
            descuentosService.eliminarDescuento(id);
            return ResponseEntity.noContent().build(); // Código 204 No Content para eliminación exitosa
        } catch (Exception e) {
            System.err.println("Error en el controlador al eliminar descuento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 si no se encuentra
        }
    }
}