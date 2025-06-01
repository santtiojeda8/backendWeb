package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Base;
import com.ecommerce.ecommerce.Repositories.BaseRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class BaseService<E extends Base, ID extends Serializable> {

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
            return baseRepository.save(entity);
        }catch(Exception e){
            throw new Exception("Error al actualizar entidad: " + e.getMessage());
        }
    }

    @Transactional
    public void eliminar(ID id) throws Exception {
        try{
            Optional<E> entityOptional = baseRepository.findById(id); // Buscar incluso si está inactivo
            if (entityOptional.isEmpty()) {
                throw new Exception("Entidad no encontrada con ID: " + id);
            }
            E entity = entityOptional.get();
            entity.setActivo(false); // Soft delete
            baseRepository.save(entity);
        }catch(Exception e){
            throw  new Exception("Error al intentar eliminar lógicamente la entidad: " + e.getMessage());
        }
    }

    @Transactional
    public E activar(ID id) throws Exception {
        try {
            Optional<E> entityOptional = baseRepository.findById(id);
            if (entityOptional.isEmpty()) {
                throw new Exception("Entidad no encontrada con ID: " + id);
            }
            E entity = entityOptional.get();
            entity.setActivo(true);
            return baseRepository.save(entity);
        } catch (Exception e) {
            throw new Exception("Error al activar entidad: " + e.getMessage());
        }
    }

    @Transactional
    public void hardDelete(ID id) throws Exception {
        try {
            baseRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception("Error al intentar eliminar físicamente la entidad: " + e.getMessage());
        }
    }
}