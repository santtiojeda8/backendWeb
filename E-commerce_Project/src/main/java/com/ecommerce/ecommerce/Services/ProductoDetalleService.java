package com.ecommerce.ecommerce.Services;



import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.enums.Color;
import com.ecommerce.ecommerce.Entities.enums.Talle;
import com.ecommerce.ecommerce.Repositories.ProductoDetalleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoDetalleService extends BaseService<ProductoDetalle, Long> {
    private final ProductoDetalleRepository productoDetalleRepository;

    public ProductoDetalleService(ProductoDetalleRepository productoDetalleRepository) {
        super(productoDetalleRepository);
        this.productoDetalleRepository = productoDetalleRepository;
    }

    public List<ProductoDetalle> findAllByProductoId(Long productoId) throws Exception {
        try {
            return productoDetalleRepository.findAllByProductoId(productoId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public ProductoDetalle findByProductoIdAndTalleAndColor(Long productoId, Talle talle, Color color) throws Exception {
        try {
            return productoDetalleRepository.findByProductoIdAndTalleAndColor(productoId, talle, color);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public List<ProductoDetalle> findAllByStockActualGreaterThan(Integer stockMinimo) throws Exception {
        try {
            return productoDetalleRepository.findAllByStockActualGreaterThan(stockMinimo);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<ProductoDetalle> filtrarPorOpciones(Long productoId, Color color, Talle talle, Integer stockMin) throws Exception {
        try {
            return productoDetalleRepository.filtrarPorOpciones(productoId, color, talle, stockMin);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Talle> obtenerTallesDisponibles(Long productoId) throws Exception {
        try {
            return productoDetalleRepository.obtenerTallesDisponibles(productoId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Color> obtenerColoresDisponibles(Long productoId) throws Exception {
        try {
            return productoDetalleRepository.obtenerColoresDisponibles(productoId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }


    public void descontarStock(Long productoDetalleId, int cantidad) throws Exception {
        try {
            ProductoDetalle detalle = productoDetalleRepository.findById(productoDetalleId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            if (detalle.getStockActual() < cantidad) {
                throw new IllegalArgumentException("Stock insuficiente");
            }
            detalle.setStockActual(detalle.getStockActual() - cantidad);
            productoDetalleRepository.save(detalle);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }


    public boolean estaDisponible(Long productoId, Talle talle, Color color) throws Exception {
        ProductoDetalle detalle = productoDetalleRepository.findByProductoIdAndTalleAndColor(productoId, talle, color);
        try {
            return detalle != null && detalle.getStockActual() > 0;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

}
