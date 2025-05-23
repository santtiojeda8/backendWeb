package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Base; // <--- Importa tu clase Base
import com.ecommerce.ecommerce.Repositories.BaseRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page; // <--- Importa Page
import org.springframework.data.domain.Pageable; // <--- Importa Pageable


import java.io.Serializable;
import java.util.List;
import java.util.Optional;

// Asegúrate de que E extienda Base para poder usar el campo 'activo'
public class BaseService<E extends Base, ID extends Serializable> {

    protected BaseRepository<E, ID> baseRepository;

    public BaseService(BaseRepository<E, ID> baseRepository){
        this.baseRepository = baseRepository;
    }

    @Transactional(readOnly = true) // <-- Añade readOnly = true para métodos de solo lectura
    public List<E> listar() throws Exception {
        try {
            // Ahora lista solo los elementos activos
            return baseRepository.findAllByActivoTrue(); // <--- Cambiado
        }catch(Exception e){
            throw new Exception("Error al listar entidades activas: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true) // <-- Añade readOnly = true
    public Page<E> listar(Pageable pageable) throws Exception { // <-- Sobrecarga para paginación
        try {
            // Ahora lista solo los elementos activos y paginados
            return baseRepository.findAllByActivoTrue(pageable); // <--- Cambiado
        }catch(Exception e){
            throw new Exception("Error al listar entidades activas paginadas: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true) // <-- Añade readOnly = true
    public E buscarPorId(ID id) throws Exception { // <--- Cambiado para retornar E directamente, no Optional
        try {
            // Busca por ID, pero solo si está activo
            Optional<E> entityOptional = baseRepository.findByIdAndActivoTrue(id); // <--- Cambiado
            if (entityOptional.isEmpty()) {
                // Puedes lanzar una excepción más específica si lo deseas, como EntityNotFoundException
                throw new Exception("Entidad no encontrada o inactiva con ID: " + id);
            }
            return entityOptional.get();
        }catch(Exception e){
            throw new Exception("Error al buscar entidad por ID: " + e.getMessage());
        }
    }

    @Transactional
    public E crear(E entity) throws Exception {
        try{
            entity.setActivo(true); // <--- Asegura que la entidad se cree como activa
            return baseRepository.save(entity);
        }catch(Exception e){
            throw new Exception("Error al crear entidad: " + e.getMessage());
        }
    }

    @Transactional
    public E actualizar(E entity) throws Exception {
        try{
            // Opcional: Asegúrate de que la entidad siga activa o si permites reactivarla
            // Si el ID de la entidad ya existe, Spring Data JPA lo actualizará (merge)
            return baseRepository.save(entity);
        }catch(Exception e){
            throw new Exception("Error al actualizar entidad: " + e.getMessage());
        }
    }

    // --- NUEVO MÉTODO PARA EL SOFT DELETE ---
    @Transactional
    public void eliminar(ID id) throws Exception { // <--- Sobreescribe el método eliminar existente
        try{
            Optional<E> entityOptional = baseRepository.findById(id); // Busca sin importar el estado 'activo'
            if (entityOptional.isEmpty()) {
                throw new Exception("Entidad no encontrada con ID: " + id);
            }
            E entity = entityOptional.get();
            entity.setActivo(false); // <--- Marcar como inactivo en lugar de borrar
            baseRepository.save(entity); // <--- Guardar el cambio
        }catch(Exception e){
            throw new Exception("Error al intentar eliminar lógicamente la entidad: " + e.getMessage());
        }
    }

    // --- OPCIONAL: Método para reactivar una entidad ---
    @Transactional
    public E activar(ID id) throws Exception {
        try {
            Optional<E> entityOptional = baseRepository.findById(id);
            if (entityOptional.isEmpty()) {
                throw new Exception("Entidad no encontrada con ID: " + id);
            }
            E entity = entityOptional.get();
            entity.setActivo(true); // Marcar como activo
            return baseRepository.save(entity);
        } catch (Exception e) {
            throw new Exception("Error al activar entidad: " + e.getMessage());
        }
    }

    // --- OPCIONAL: Método para el borrado físico (si realmente lo necesitas para algo específico) ---
    // NO expongas este método a menos que sea absolutamente necesario y bien controlado.
    @Transactional
    public void hardDelete(ID id) throws Exception {
        try {
            baseRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception("Error al intentar eliminar físicamente la entidad: " + e.getMessage());
        }
    }
}