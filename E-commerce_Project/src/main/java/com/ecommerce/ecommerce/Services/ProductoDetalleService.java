package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.Color;
import com.ecommerce.ecommerce.Entities.Talle;
import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Repositories.ProductoDetalleRepository;
import com.ecommerce.ecommerce.Repositories.ColorRepository;
import com.ecommerce.ecommerce.Repositories.TalleRepository;
import com.ecommerce.ecommerce.Repositories.ProductoRepository;
import com.ecommerce.ecommerce.dto.ProductoDetalleDTO; // ¡Usamos este para todo ahora!

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoDetalleService extends BaseService<ProductoDetalle, Long> {
    private final ProductoDetalleRepository productoDetalleRepository;
    private final ColorRepository colorRepository;
    private final TalleRepository talleRepository;
    private final ProductoRepository productoRepository;

    public ProductoDetalleService(ProductoDetalleRepository productoDetalleRepository,
                                  ColorRepository colorRepository,
                                  TalleRepository talleRepository,
                                  ProductoRepository productoRepository) {
        super(productoDetalleRepository);
        this.productoDetalleRepository = productoDetalleRepository;
        this.colorRepository = colorRepository;
        this.talleRepository = talleRepository;
        this.productoRepository = productoRepository;
    }

    // --- Métodos que ya devuelven List<ProductoDetalleDTO> o ProductoDetalleDTO ---

    @Transactional(readOnly = true)
    public List<ProductoDetalleDTO> findAllByProductoId(Long productoId) throws Exception {
        try {
            List<ProductoDetalle> entities = productoDetalleRepository.findAllByProductoIdAndActivoTrue(productoId);
            return entities.stream()
                    .map(ProductoDetalleDTO::new) // Usa el constructor de tu DTO
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al buscar ProductoDetalles por ID de Producto: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public ProductoDetalleDTO findByProductoIdAndTalleAndColor(Long productoId, String talleNombre, String colorNombre) throws Exception {
        try {
            Color color = colorRepository.findByNombreColorAndActivoTrue(colorNombre)
                    .orElseThrow(() -> new EntityNotFoundException("Color no encontrado o inactivo: " + colorNombre));
            Talle talle = talleRepository.findByNombreTalleAndActivoTrue(talleNombre)
                    .orElseThrow(() -> new EntityNotFoundException("Talle no encontrado o inactivo: " + talleNombre));

            Optional<ProductoDetalle> detalleOptional = productoDetalleRepository.findByProductoIdAndTalleAndColorAndActivoTrue(productoId, talle, color);
            if (detalleOptional.isEmpty()) {
                throw new EntityNotFoundException("ProductoDetalle no encontrado o inactivo para el ProductoId: " + productoId + ", Talle: " + talleNombre + ", Color: " + colorNombre);
            }
            return new ProductoDetalleDTO(detalleOptional.get()); // Usa el constructor de tu DTO
        } catch (Exception e) {
            throw new Exception("Error al buscar ProductoDetalle por ProductoId, Talle y Color: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDetalleDTO> findAllByStockActualGreaterThan(Integer stockMinimo) throws Exception {
        try {
            List<ProductoDetalle> entities = productoDetalleRepository.findAllByStockActualGreaterThanAndActivoTrue(stockMinimo);
            return entities.stream()
                    .map(ProductoDetalleDTO::new) // Usa el constructor de tu DTO
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al buscar ProductoDetalles con stock mayor a: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDetalleDTO> filtrarPorOpciones(Long productoId, String colorNombre, String talleNombre, Integer stockMin) throws Exception {
        try {
            List<ProductoDetalle> entities = productoDetalleRepository.filtrarPorOpciones(productoId, colorNombre, talleNombre, stockMin);
            return entities.stream()
                    .map(ProductoDetalleDTO::new) // Usa el constructor de tu DTO
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al filtrar ProductoDetalles por opciones: " + e.getMessage(), e);
        }
    }

    // Métodos que no devuelven DTOs (List<String> o boolean)
    @Transactional(readOnly = true)
    public List<String> obtenerTallesDisponibles(Long productoId) throws Exception {
        try {
            return productoDetalleRepository.obtenerNombresTallesDisponibles(productoId);
        } catch (Exception e) {
            throw new Exception("Error al obtener talles disponibles: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<String> obtenerColoresDisponibles(Long productoId) throws Exception {
        try {
            return productoDetalleRepository.obtenerNombresColoresDisponibles(productoId);
        } catch (Exception e) {
            throw new Exception("Error al obtener colores disponibles: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void descontarStock(Long productoDetalleId, int cantidad) throws Exception {
        try {
            ProductoDetalle detalle = productoDetalleRepository.findByIdAndActivoTrue(productoDetalleId)
                    .orElseThrow(() -> new EntityNotFoundException("ProductoDetalle no encontrado o inactivo con ID: " + productoDetalleId));
            if (detalle.getStockActual() < cantidad) {
                throw new IllegalArgumentException("Stock insuficiente para el ProductoDetalle ID: " + productoDetalleId);
            }
            detalle.setStockActual(detalle.getStockActual() - cantidad);
            productoDetalleRepository.save(detalle);
        } catch (Exception e) {
            throw new Exception("Error al descontar stock: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public boolean estaDisponible(Long productoId, String talleNombre, String colorNombre) throws Exception {
        try {
            Color color = colorRepository.findByNombreColorAndActivoTrue(colorNombre)
                    .orElseThrow(() -> new EntityNotFoundException("Color no encontrado o inactivo: " + colorNombre));
            Talle talle = talleRepository.findByNombreTalleAndActivoTrue(talleNombre)
                    .orElseThrow(() -> new EntityNotFoundException("Talle no encontrado o inactivo: " + talleNombre));

            Optional<ProductoDetalle> detalleOptional = productoDetalleRepository.findByProductoIdAndTalleAndColorAndActivoTrue(productoId, talle, color);
            return detalleOptional.isPresent() && detalleOptional.get().getStockActual() > 0;
        } catch (Exception e) {
            throw new Exception("Error al verificar disponibilidad: " + e.getMessage(), e);
        }
    }

    // --- Métodos para crear y actualizar usando ProductoDetalleDTO como entrada y salida ---

    @Transactional
    public ProductoDetalle crearDesdeDTO(ProductoDetalleDTO dto) throws Exception { // Recibe ProductoDetalleDTO
        ProductoDetalle newDetalle = new ProductoDetalle();
        newDetalle.setPrecioCompra(dto.getPrecioCompra());
        newDetalle.setStockActual(dto.getStockActual());
        newDetalle.setStockMaximo(dto.getStockMaximo());
        newDetalle.setActivo(dto.isActivo());

        // Cargar y asignar Color usando colorId del DTO de entrada
        if (dto.getColorId() != null) {
            Color color = colorRepository.findById(dto.getColorId())
                    .orElseThrow(() -> new EntityNotFoundException("Color no encontrado con ID: " + dto.getColorId()));
            newDetalle.setColor(color);
        } else {
            throw new IllegalArgumentException("El ID del color no puede ser nulo.");
        }

        // Cargar y asignar Talle usando talleId del DTO de entrada
        if (dto.getTalleId() != null) {
            Talle talle = talleRepository.findById(dto.getTalleId())
                    .orElseThrow(() -> new EntityNotFoundException("Talle no encontrado con ID: " + dto.getTalleId()));
            newDetalle.setTalle(talle);
        } else {
            throw new IllegalArgumentException("El ID del talle no puede ser nulo.");
        }

        // Cargar y asignar Producto usando productoId del DTO de entrada (obligatorio)
        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + dto.getProductoId()));
            newDetalle.setProducto(producto);
        } else {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo para un ProductoDetalle.");
        }

        return productoDetalleRepository.save(newDetalle);
    }

    @Transactional
    public ProductoDetalle actualizarDesdeDTO(Long id, ProductoDetalleDTO dto) throws Exception { // Recibe ProductoDetalleDTO
        ProductoDetalle existingDetalle = productoDetalleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductoDetalle no encontrado para actualizar con ID: " + id));

        // Actualizar campos básicos
        existingDetalle.setPrecioCompra(dto.getPrecioCompra());
        existingDetalle.setStockActual(dto.getStockActual());
        existingDetalle.setStockMaximo(dto.getStockMaximo());
        existingDetalle.setActivo(dto.isActivo());

        // Cargar y asignar Color usando colorId del DTO de entrada
        if (dto.getColorId() != null) {
            Color color = colorRepository.findById(dto.getColorId())
                    .orElseThrow(() -> new EntityNotFoundException("Color no encontrado con ID: " + dto.getColorId()));
            existingDetalle.setColor(color);
        } else {
            throw new IllegalArgumentException("El ID del color no puede ser nulo.");
        }

        // Cargar y asignar Talle usando talleId del DTO de entrada
        if (dto.getTalleId() != null) {
            Talle talle = talleRepository.findById(dto.getTalleId())
                    .orElseThrow(() -> new EntityNotFoundException("Talle no encontrado con ID: " + dto.getTalleId()));
            existingDetalle.setTalle(talle);
        } else {
            throw new IllegalArgumentException("El ID del talle no puede ser nulo.");
        }

        // Actualizar la asociación con Producto solo si el ID es diferente y no nulo
        if (dto.getProductoId() != null &&
                (existingDetalle.getProducto() == null || !existingDetalle.getProducto().getId().equals(dto.getProductoId()))) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + dto.getProductoId()));
            existingDetalle.setProducto(producto);
        } else if (existingDetalle.getProducto() == null && dto.getProductoId() == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo para un ProductoDetalle.");
        }

        return productoDetalleRepository.save(existingDetalle);
    }
}