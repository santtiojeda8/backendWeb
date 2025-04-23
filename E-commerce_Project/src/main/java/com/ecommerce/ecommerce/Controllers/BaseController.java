package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Services.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BaseController<E> {

    @Autowired
    private BaseService<E> baseService;

    // Obtener todas las entidades
    @GetMapping("/entities")
    public ResponseEntity<List<E>> getAllEntities() {
        try {
            List<E> entities = baseService.findAll();
            return new ResponseEntity<>(entities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener una entidad por ID
    @GetMapping("/entities/{id}")
    public ResponseEntity<E> getEntityById(@PathVariable Long id) {
        try {
            E entity = baseService.finById(id);
            if (entity == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(entity, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Crear una nueva entidad
    @PostMapping("/entities")
    public ResponseEntity<E> createEntity(@RequestBody E entity) {
        try {
            E createdEntity = baseService.save(entity);
            return new ResponseEntity<>(createdEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualizar una entidad
    @PutMapping("/entities/{id}")
    public ResponseEntity<E> updateEntity(@PathVariable Long id, @RequestBody E newEntity) {
        try {
            E updatedEntity = baseService.update(id, newEntity);
            if (updatedEntity == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar una entidad por ID
    @DeleteMapping("/entities/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable Long id) {
        try {
            boolean isDeleted = baseService.delete(id);
            if (!isDeleted) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
