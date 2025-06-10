package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Services.DescuentosService;
import com.ecommerce.ecommerce.dto.DescuentoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/descuentos")
// ¡Importante! Mantén esta clase SIN extender BaseController para manejar DTOs.
public class DescuentoController {

    private final DescuentosService descuentosService;

    @Autowired
    public DescuentoController(DescuentosService descuentosService) {
        this.descuentosService = descuentosService;
    }

    // Endpoint para obtener TODOS los descuentos (activos e inactivos) para la administración
    @GetMapping // Ya no es necesario "/all" si este es el listado principal para el panel.
    // Si necesitas un listado solo de activos para el público, entonces este debería ser "/all" y crear otro "@GetMapping" sin "/all".
    public ResponseEntity<List<DescuentoDTO>> getAllDescuentos() {
        try {
            // Llama a listarDTOs() que en el servicio ya fue modificado para usar findAll()
            List<DescuentoDTO> descuentos = descuentosService.listarDTOs();
            return ResponseEntity.ok(descuentos);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener todos los descuentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para obtener un descuento por ID (incluye inactivos para edición)
    @GetMapping("/{id}")
    public ResponseEntity<DescuentoDTO> getDescuentoById(@PathVariable Long id) {
        try {
            // Llama a obtenerDescuentoDTOPorId() que en el servicio ya fue modificado para usar buscarPorIdIncluyendoInactivos()
            DescuentoDTO descuento = descuentosService.obtenerDescuentoDTOPorId(id);
            return ResponseEntity.ok(descuento);
        } catch (Exception e) {
            System.err.println("Error en el controlador al obtener descuento por ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint para CREAR un nuevo descuento
    @PostMapping
    public ResponseEntity<DescuentoDTO> createDescuento(@RequestBody DescuentoDTO dto) {
        try {
            DescuentoDTO savedDto = descuentosService.crearDescuento(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
        } catch (IllegalArgumentException e) { // Captura específicamente errores de validación del servicio
            System.err.println("Error de validación al crear descuento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Error en el controlador al crear descuento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint para ACTUALIZAR un descuento existente
    @PutMapping("/{id}")
    public ResponseEntity<DescuentoDTO> updateDescuento(@PathVariable Long id, @RequestBody DescuentoDTO dto) {
        try {
            DescuentoDTO updatedDto = descuentosService.actualizarDescuento(id, dto);
            return ResponseEntity.ok(updatedDto);
        } catch (IllegalArgumentException e) { // Captura específicamente errores de validación del servicio
            System.err.println("Error de validación al actualizar descuento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Error en el controlador al actualizar descuento (ID: " + id + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- NUEVO Endpoint para TOGGLE STATUS ---
    // Este endpoint es el que usarás en el frontend para activar/desactivar un descuento.
    @PutMapping("/toggleStatus/{id}")
    public ResponseEntity<DescuentoDTO> toggleDescuentoStatus(@PathVariable Long id, @RequestParam boolean currentStatus) {
        try {
            // Llama al método toggleDiscountStatus del servicio.
            // Este método ya llama a BaseService.toggleStatus y refresca la lista.
            DescuentoDTO updatedDto = descuentosService.toggleDiscountStatus(id, currentStatus);
            return ResponseEntity.ok(updatedDto); // Devuelve el DTO actualizado
        } catch (Exception e) {
            System.err.println("Error en el controlador al cambiar el estado del descuento (ID: " + id + "): " + e.getMessage());
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}