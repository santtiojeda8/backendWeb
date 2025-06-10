package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Base;
import com.ecommerce.ecommerce.Repositories.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<E extends Base, ID extends Serializable> {

    protected BaseRepository<E, ID> baseRepository;

    public BaseService(BaseRepository<E, ID> baseRepository){
        this.baseRepository = baseRepository;
    }

    @Transactional(readOnly = true)
    public List<E> listar() throws Exception {
        try {
            return baseRepository.findAllByActivoTrue();
        }catch(Exception e){
            throw new Exception("Error al listar entidades activas: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<E> listar(Pageable pageable) throws Exception {
        try {
            return baseRepository.findAllByActivoTrue(pageable);
        }catch(Exception e){
            throw new Exception("Error al listar entidades activas paginadas: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public E buscarPorId(ID id) throws Exception {
        try {
            Optional<E> entityOptional = baseRepository.findByIdAndActivoTrue(id);
            if (entityOptional.isEmpty()) {
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
            entity.setActivo(true); // Asegurarse de que esté activo al crear
            return baseRepository.save(entity);
        }catch(Exception e){
            throw new Exception("Error al crear entidad: " + e.getMessage());
        }
    }

    @Transactional
    public E actualizar(E entity) throws Exception {
        try{
            // Opcional: Asegúrate de que la entidad exista y puedas actualizarla.
            // Si tu 'actualizar' solo hace save(), Hibernate manejará el update si el ID existe.
            // Podrías añadir una validación con findById(entity.getId()) aquí si quieres más control.
            return baseRepository.save(entity);
        }catch(Exception e){
            throw new Exception("Error al actualizar entidad: " + e.getMessage());
        }
    }

    /**
     * Realiza un "soft delete" (eliminación lógica) de una entidad, marcándola como inactiva.
     * Este método NO se usará directamente para el toggle de estado en el frontend,
     * pero sigue siendo útil si tienes un botón de "eliminar" que solo desactiva.
     * Retorna la entidad actualizada (inactiva).
     */
    @Transactional
    public E eliminar(ID id) throws Exception {
        try{
            Optional<E> entityOptional = baseRepository.findById(id);
            if (entityOptional.isEmpty()) {
                throw new Exception("Entidad no encontrada con ID: " + id);
            }
            E entity = entityOptional.get();
            entity.setActivo(false); // Soft delete
            return baseRepository.save(entity); // Retorna la entidad con activo: false
        }catch(Exception e){
            throw  new Exception("Error al intentar eliminar lógicamente la entidad: " + e.getMessage());
        }
    }

    /**
     * Activa una entidad que previamente estaba inactiva.
     * Retorna la entidad actualizada (activa).
     */
    @Transactional
    public E activar(ID id) throws Exception {
        try {
            Optional<E> entityOptional = baseRepository.findById(id);
            if (entityOptional.isEmpty()) {
                throw new Exception("Entidad no encontrada con ID: " + id);
            }
            E entity = entityOptional.get();
            entity.setActivo(true);
            return baseRepository.save(entity); // Retorna la entidad con activo: true
        } catch (Exception e) {
            throw new Exception("Error al activar entidad: " + e.getMessage());
        }
    }

    /**
     * Realiza una eliminación física de la entidad de la base de datos.
     * Usar con precaución.
     */
    @Transactional
    public void hardDelete(ID id) throws Exception {
        try {
            baseRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception("Error al intentar eliminar físicamente la entidad: " + e.getMessage());
        }
    }

    // --- MÉTODOS PARA ADMINISTRADORES (incluyen inactivos) ---

    /**
     * Obtiene una lista de todas las entidades, incluyendo activas e inactivas.
     * Ideal para vistas de administración.
     */
    @Transactional(readOnly = true)
    public List<E> findAll() throws Exception {
        try {
            return baseRepository.findAll(); // Obtiene todas las entidades, sin filtrar por activo
        } catch (Exception e) {
            throw new Exception("Error al obtener todas las entidades (incluyendo inactivas): " + e.getMessage());
        }
    }

    /**
     * Busca una entidad por su ID, sin importar si está activa o inactiva.
     * Útil para vistas de administración donde se necesita acceso a entidades inactivas.
     */
    @Transactional(readOnly = true)
    public E buscarPorIdIncluyendoInactivos(ID id) throws Exception {
        try {
            return baseRepository.findById(id) // Busca por ID sin importar el estado 'activo'
                    .orElseThrow(() -> new Exception("Entidad no encontrada con ID: " + id));
        } catch (Exception e) {
            throw new Exception("Error al buscar entidad por ID (incluyendo inactivos): " + e.getMessage());
        }
    }

    /**
     * Cambia el estado 'activo' de una entidad (lo invierte).
     * Este es el método central para el frontend.
     * @param id El ID de la entidad.
     * @param currentStatus El estado actual de la entidad (true si está activa, false si está inactiva).
     * Se usa para determinar el nuevo estado.
     * @return La entidad con su estado 'activo' actualizado.
     * @throws Exception Si la entidad no se encuentra.
     */
    @Transactional
    public E toggleStatus(ID id, boolean currentStatus) throws Exception {
        try {
            // Busca la entidad por ID, sin importar si está activa o inactiva.
            Optional<E> entityOptional = baseRepository.findById(id);
            if (entityOptional.isEmpty()) {
                throw new Exception("Entidad no encontrada con ID: " + id);
            }
            E entity = entityOptional.get();
            // Invierte el estado 'activo'
            entity.setActivo(!currentStatus);
            return baseRepository.save(entity); // Guarda y devuelve la entidad actualizada
        } catch (Exception e) {
            throw new Exception("Error al cambiar el estado de la entidad: " + e.getMessage());
        }
    }
}