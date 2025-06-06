package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Base;
import com.ecommerce.ecommerce.Services.BaseService;
import org.springframework.http.HttpStatus; // Importar HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class BaseController<E extends Base, ID extends Serializable> {

    protected BaseService<E, ID> service;

    public BaseController(BaseService<E, ID> service){
        this.service = service;
    }

    // --- Métodos de Listado (listar solo activos) ---
    @GetMapping()
    public ResponseEntity<List<E>> listar() { // Quitamos 'throws Exception' para manejo interno
        try {
            List<E> entities = service.listar(); // Este método ahora devuelve solo los activos
            return ResponseEntity.ok(entities);
        } catch (Exception e) {
            System.err.println("Error al listar entidades: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Métodos de Búsqueda por ID (buscar solo activos) ---
    @GetMapping("/{id}")
    public ResponseEntity<E> buscarPorId(@PathVariable ID id) { // Cambiado a ResponseEntity<E>
        try {
            E entity = service.buscarPorId(id); // Este método ahora busca solo activos y lanza excepción si no encuentra o está inactivo
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            System.err.println("Error al buscar entidad por ID " + id + ": " + e.getMessage());
            // Si la excepción es por no encontrado o inactivo, podemos devolver 404
            if (e.getMessage().contains("no encontrada o inactiva")) { // Puedes refinar el mensaje de excepción en BaseService
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Método de Creación (crea como activo) ---
    @PostMapping()
    public ResponseEntity<E> crear(@RequestBody E entity) {
        try {
            E entidadCreada = service.crear(entity); // El servicio ya asegura que se crea como activo
            return ResponseEntity.status(HttpStatus.CREATED).body(entidadCreada); // 201 Created
        } catch (Exception e) {
            System.err.println("Error al crear entidad: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Método de Actualización (actualiza la entidad) ---
    @PutMapping("/{id}") // Añadimos el ID en la URL para una actualización RESTful
    public ResponseEntity<E> actualizar(@PathVariable ID id, @RequestBody E entity) { // Recibe el ID y la entidad a actualizar
        try {
            // Asegúrate de que el ID del path coincida con el ID de la entidad, si es necesario
            // entity.setId(id); // Esto podría ser necesario dependiendo de tu lógica de actualización

            E entidadAct = service.actualizar(entity); // El servicio actualiza el estado existente
            return ResponseEntity.ok(entidadAct);
        } catch (Exception e) {
            System.err.println("Error al actualizar entidad con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            // Puedes refinar esto para devolver 404 si la excepción indica "no encontrado"
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Método de "Eliminación" Lógica (Soft Delete) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable ID id) { // Cambiado a ResponseEntity<?>
        try {
            service.eliminar(id); // Este método ahora realiza el soft delete
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
        } catch (Exception e) {
            System.err.println("Error al eliminar entidad con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            // Puedes refinar esto para devolver 404 si la entidad no fue encontrada
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // --- OPCIONAL: Endpoint para reactivar una entidad ---
    @PutMapping("/activar/{id}")
    public ResponseEntity<E> activar(@PathVariable ID id) {
        try {
            E entidadActivada = service.activar(id);
            return ResponseEntity.ok(entidadActivada);
        } catch (Exception e) {
            System.err.println("Error al activar entidad con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
