package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Provincia;
import com.ecommerce.ecommerce.Repositories.ProvinciaRepository;
import com.ecommerce.ecommerce.dto.ProvinciaDTO; // ¡Importamos el DTO!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Necesario para buscarPorId
import java.util.stream.Collectors; // Necesario para stream().map().collect()

@Service
public class ProvinciaService extends BaseService<Provincia, Long> {

    private final ProvinciaRepository provinciaRepository;

    @Autowired
    public ProvinciaService(ProvinciaRepository provinciaRepository) {
        super(provinciaRepository); // Llama al constructor del BaseService
        this.provinciaRepository = provinciaRepository; // Guardamos la referencia del repositorio
    }

    // --- Métodos de Mapeo ---
    // Convierte una entidad Provincia a un DTO
    public ProvinciaDTO mapProvinciaToDTO(Provincia provincia) {
        if (provincia == null) {
            return null;
        }
        return ProvinciaDTO.builder()
                .id(provincia.getId())
                .nombre(provincia.getNombre())
                .build();
    }

    // Convierte un DTO a una entidad Provincia (útil para crear/actualizar)
    public Provincia mapDTOToProvincia(ProvinciaDTO dto) {
        if (dto == null) {
            return null;
        }
        Provincia provincia = new Provincia();
        if (dto.getId() != null) { // Solo asigna el ID si está presente (para actualizaciones)
            provincia.setId(dto.getId());
        }
        provincia.setNombre(dto.getNombre());
        // El campo 'activo' se manejará en BaseService.crear/actualizar
        return provincia;
    }

    // --- Métodos del servicio que ahora operan con DTOs ---

    @Transactional(readOnly = true)
    public List<ProvinciaDTO> findAllDTO() throws Exception {
        List<Provincia> provincias = super.listar(); // Llama al método del BaseService para obtener entidades activas
        return provincias.stream()
                .map(this::mapProvinciaToDTO) // Mapea cada entidad a su DTO
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProvinciaDTO findByIdDTO(Long id) throws Exception {
        Provincia provincia = super.buscarPorId(id); // Llama al método del BaseService para buscar una entidad activa
        return mapProvinciaToDTO(provincia); // Mapea la entidad encontrada a su DTO
    }

    @Transactional
    public ProvinciaDTO saveFromDTO(ProvinciaDTO dto) throws Exception {
        Provincia provinciaToSave = mapDTOToProvincia(dto); // Convierte el DTO a entidad
        Provincia savedProvincia = super.crear(provinciaToSave); // Guarda la entidad (BaseService la marca como activa)
        return mapProvinciaToDTO(savedProvincia); // Devuelve el DTO de la entidad guardada
    }

    @Transactional
    public ProvinciaDTO updateFromDTO(Long id, ProvinciaDTO dto) throws Exception {
        // Primero, busca la entidad existente para asegurarte de que existe y está activa
        Provincia existingProvincia = super.buscarPorId(id); // Lanza excepción si no se encuentra o está inactiva

        // Actualiza solo los campos que pueden ser modificados desde el DTO
        existingProvincia.setNombre(dto.getNombre());
        // Si hay otros campos, actualízalos aquí.

        Provincia updatedProvincia = super.actualizar(existingProvincia); // Guarda los cambios en la entidad
        return mapProvinciaToDTO(updatedProvincia); // Devuelve el DTO de la entidad actualizada
    }

    @Transactional
    public void delete(Long id) throws Exception {
        super.eliminar(id); // Llama al soft delete del BaseService
    }

    @Transactional
    public ProvinciaDTO activateFromService(Long id) throws Exception {
        Provincia activatedProvincia = super.activar(id); // Llama al método de activar del BaseService
        return mapProvinciaToDTO(activatedProvincia); // Devuelve el DTO de la entidad activada
    }
}