package com.ecommerce.ecommerce.Services;

import java.util.List;

public interface BaseService<E>{
    // Mostrar todas las entidades
    public List<E> findAll() throws Exception;

    // Mostrar entidad por Id
    public E finById(Long id) throws Exception;

    // Crear Entidad
    public E save(E entity) throws Exception;

    // Actualizar Entidad
    public E update(Long id, E newEntity) throws Exception;

    // Eliminar Entidad por Id
    public boolean delete(Long id) throws Exception;
}
