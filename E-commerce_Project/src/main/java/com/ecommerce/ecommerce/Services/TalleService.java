package com.ecommerce.ecommerce.Services;
import com.ecommerce.ecommerce.Entities.Talle;
import com.ecommerce.ecommerce.Repositories.TalleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TalleService extends BaseService<Talle, Long> {

    private final TalleRepository talleRepository;

    public TalleService(TalleRepository talleRepository) {
        super(talleRepository);
        this.talleRepository = talleRepository;
    }

    // Método específico para buscar un talle por su nombre
    @Transactional(readOnly = true)
    public Talle buscarPorNombre(String nombreTalle) throws Exception {
        try {
            Optional<Talle> talleOptional = talleRepository.findByNombreTalleAndActivoTrue(nombreTalle);
            if (talleOptional.isEmpty()) {
                throw new Exception("Talle no encontrado o inactivo con nombre: " + nombreTalle);
            }
            return talleOptional.get();
        } catch (Exception e) {
            throw new Exception("Error al buscar talle por nombre: " + e.getMessage());
        }
    }


}
