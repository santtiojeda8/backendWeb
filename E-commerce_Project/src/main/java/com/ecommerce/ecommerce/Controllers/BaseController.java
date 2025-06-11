package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Base;
import com.ecommerce.ecommerce.Services.BaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

public abstract class BaseController<E extends Base, ID extends Serializable> {

    protected BaseService<E, ID> service;

    public BaseController(BaseService<E, ID> service){
        this.service = service;
    }

    // --- Métodos de Listado (listar solo activos) ---
    @GetMapping()
    public ResponseEntity<List<E>> listar() {
        try {
            List<E> entities = service.listar(); // Este método ahora devuelve solo los activos
            return ResponseEntity.ok(entities);
        } catch (Exception e) {
            System.err.println("Error al listar entidades: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- NUEVO MÉTODO: Listar TODAS las entidades (activas e inactivas) para administración ---
    @GetMapping("/all") // Por ejemplo, /talles/all o /categorias/all
    public ResponseEntity<List<E>> listarAll() {
        try {
            List<E> entities = service.findAll(); // Llama al método findAll() de BaseService
            return ResponseEntity.ok(entities);
        } catch (Exception e) {
            System.err.println("Error al listar todas las entidades: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Métodos de Búsqueda por ID (buscar solo activos) ---
    @GetMapping("/{id}")
    public ResponseEntity<E> buscarPorId(@PathVariable ID id) {
        try {
            E entity = service.buscarPorId(id);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            System.err.println("Error al buscar entidad por ID " + id + ": " + e.getMessage());
            if (e.getMessage().contains("no encontrada o inactiva")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Método de Creación (crea como activo) ---
    @PostMapping()
    public ResponseEntity<E> crear(@RequestBody E entity) {
        try {
            E entidadCreada = service.crear(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(entidadCreada);
        } catch (Exception e) {
            System.err.println("Error al crear entidad: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Método de Actualización (actualiza la entidad) ---
    @PutMapping("/{id}")
    public ResponseEntity<E> actualizar(@PathVariable ID id, @RequestBody E entity) {
        try {
            // Asegúrate de que el ID de la entidad en el body coincida con el ID del path
            // Esto es crucial para la seguridad y la consistencia
            // Si tu entidad no mapea el ID del body automáticamente, puedes hacerlo aquí:
            // entity.setId(id);
            E entidadAct = service.actualizar(entity);
            return ResponseEntity.ok(entidadAct);
        } catch (Exception e) {
            System.err.println("Error al actualizar entidad con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint para realizar un "soft delete" (eliminación lógica) de una entidad.
     * Devuelve la entidad con su estado 'activo' en 'false'.
     * @param id El ID de la entidad a eliminar lógicamente.
     * @return ResponseEntity con la entidad actualizada o un error.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<E> eliminar(@PathVariable ID id) {
        try {
            E entity = service.eliminar(id); // service.eliminar ahora devuelve la entidad
            return ResponseEntity.ok(entity); // Devuelve la entidad con activo: false
        } catch (Exception e) {
            System.err.println("Error al eliminar entidad con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retorna null en body para 404
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint para activar una entidad previamente inactiva.
     * Devuelve la entidad con su estado 'activo' en 'true'.
     * @param id El ID de la entidad a activar.
     * @return ResponseEntity con la entidad actualizada o un error.
     */
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

    /**
     * Endpoint unificado para alternar el estado 'activo' de una entidad (activar/desactivar).
     * Utiliza el 'currentStatus' recibido del frontend para determinar el nuevo estado.
     * Retorna la entidad con su estado 'activo' actualizado.
     * @param id El ID de la entidad.
     * @param currentStatus El estado actual de la entidad que se recibe del frontend.
     * @return ResponseEntity con la entidad actualizada o un error.
     */
    @PutMapping("/toggleStatus/{id}")
    public ResponseEntity<E> toggleStatus(@PathVariable ID id, @RequestParam boolean currentStatus) {
        try {
            E updatedEntity = service.toggleStatus(id, currentStatus);
            return ResponseEntity.ok(updatedEntity); // Devuelve la entidad actualizada
        } catch (Exception e) {
            System.err.println("Error al cambiar el estado de la entidad con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}