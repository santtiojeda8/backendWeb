package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Repositories.LocalidadRepository;
import com.ecommerce.ecommerce.Repositories.ProvinciaRepository; // Posiblemente necesario para validaciones si no confías en el ID
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalidadService extends BaseService<Localidad, Long> {

    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository; // Necesario si findByProvinciaId usa el objeto Provincia

    public LocalidadService(LocalidadRepository localidadRepository, ProvinciaRepository provinciaRepository) {
        super(localidadRepository);
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository; // Inyecta
    }

    // findAll ya debería venir de BaseService. Si no, lo añadirías aquí:
    // public List<Localidad> findAll() throws Exception {
    //     try {
    //         return localidadRepository.findAll();
    //     } catch (Exception e) {
    //         throw new Exception("Error al obtener todas las localidades: " + e.getMessage());
    //     }
    // }

    public List<Localidad> findByProvinciaId(Long provinciaId) throws Exception {
        try {
            // Opcional: verificar que la provincia exista antes de buscar sus localidades
            // provinciaRepository.findById(provinciaId)
            //     .orElseThrow(() -> new Exception("Provincia no encontrada con ID: " + provinciaId));

            return localidadRepository.findByProvinciaId(provinciaId);
        } catch (Exception e) {
            throw new Exception("Error al obtener localidades para la provincia con ID: " + provinciaId + ". " + e.getMessage());
        }
    }
}