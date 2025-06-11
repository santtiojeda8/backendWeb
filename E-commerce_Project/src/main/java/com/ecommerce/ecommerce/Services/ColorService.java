package com.ecommerce.ecommerce.Services;
import com.ecommerce.ecommerce.Entities.Color;
import com.ecommerce.ecommerce.Repositories.ColorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ColorService extends BaseService<Color, Long> {

    private final ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) {
        super(colorRepository);
        this.colorRepository = colorRepository;
    }

    // Método específico para buscar un color por su nombre
    @Transactional(readOnly = true)
    public Color buscarPorNombre(String nombreColor) throws Exception {
        try {
            Optional<Color> colorOptional = colorRepository.findByNombreColorAndActivoTrue(nombreColor);
            if (colorOptional.isEmpty()) {
                throw new Exception("Color no encontrado o inactivo con nombre: " + nombreColor);
            }
            return colorOptional.get();
        } catch (Exception e) {
            throw new Exception("Error al buscar color por nombre: " + e.getMessage());
        }
    }


}