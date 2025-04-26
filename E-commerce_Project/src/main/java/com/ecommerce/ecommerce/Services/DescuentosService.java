package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Descuento;
import com.ecommerce.ecommerce.Repositories.DescuentosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DescuentosService extends BaseService<Descuento, Long> {

    private final DescuentosRepository descuentosRepository;

    public DescuentosService(DescuentosRepository descuentosRepository) {
        super(descuentosRepository);
        this.descuentosRepository = descuentosRepository;
    }

    @Override
    public Descuento crear(Descuento descuento) throws Exception {
        validarDescuento(descuento); // Validación antes de crear
        try {
            return super.crear(descuento); // Uso del método base para crear
        } catch (Exception e) {
            throw new Exception("Error al crear el descuento: " + e.getMessage());
        }
    }

    @Override
    public Descuento actualizar(Descuento descuento) throws Exception {
        validarDescuento(descuento); // Validación antes de actualizar
        try {
            return super.actualizar(descuento); // Uso del método base para actualizar
        } catch (Exception e) {
            throw new Exception("Error al actualizar el descuento: " + e.getMessage());
        }
    }

    // Método para obtener descuento por ID
    public Optional<Descuento> obtenerPorIdDescuento(Long idDescuento) throws Exception {
        try {
            // Uso del método findById que ahora es el método estándar
            return descuentosRepository.findById(idDescuento);
        } catch (Exception e) {
            throw new Exception("No se pudo obtener el descuento por ID de descuento: " + e.getMessage());
        }
    }

    // Método para obtener descuentos por ID de producto
    public List<Descuento> obtenerPorIdProducto(Long idProducto) throws Exception {
        try {
            // Uso del método findAllByProductoId
            return descuentosRepository.findAllByProductoId(idProducto);
        } catch (Exception e) {
            throw new Exception("No se pudieron obtener los descuentos por ID de producto: " + e.getMessage());
        }
    }

    // Método de validación de descuento
    private void validarDescuento(Descuento descuento) throws Exception {
        try {
            // Validación de fechas
            if (descuento.getFechaDesde() == null || descuento.getFechaHasta() == null ||
                    descuento.getFechaDesde().isAfter(descuento.getFechaHasta())) {
                throw new Exception("Las fechas del descuento son inválidas.");
            }

            // Validación de horas
            if (descuento.getHoraDesde() == null || descuento.getHoraHasta() == null ||
                    !descuento.getHoraDesde().isBefore(descuento.getHoraHasta())) {
                throw new Exception("Las horas del descuento son inválidas.");
            }

            // Validación de precio promocional
            if (descuento.getPrecioPromocional() == null || descuento.getPrecioPromocional() <= 0) {
                throw new Exception("El precio promocional debe ser mayor a 0.");
            }

            // Validación de denominación
            if (descuento.getDenominacion() == null || descuento.getDenominacion().isEmpty()) {
                throw new Exception("La denominación no puede estar vacía.");
            }

        } catch (Exception e) {
            throw new Exception("Error al validar el descuento: " + e.getMessage());
        }
    }
}
