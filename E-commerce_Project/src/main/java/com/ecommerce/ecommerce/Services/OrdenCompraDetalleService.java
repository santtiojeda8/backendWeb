package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Repositories.OrdenCompraDetalleRepository;
import com.ecommerce.ecommerce.Repositories.ProductoDetalleRepository;
import com.ecommerce.ecommerce.dto.OrdenCompraDetalleDTO;

import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdenCompraDetalleService extends BaseService<OrdenCompraDetalle, Long> {

    private final OrdenCompraDetalleRepository ordenCompraDetalleRepository;
    private final ProductoDetalleService productoDetalleService;
    private final ProductoDetalleRepository productoDetalleRepository;

    @Autowired
    public OrdenCompraDetalleService(OrdenCompraDetalleRepository ordenCompraDetalleRepository,
                                     ProductoDetalleService productoDetalleService,
                                     ProductoDetalleRepository productoDetalleRepository) {
        super(ordenCompraDetalleRepository);
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
        this.productoDetalleService = productoDetalleService;
        this.productoDetalleRepository = productoDetalleRepository;
    }

    // --- Métodos de Mapeo de Entidad a DTO ---
    public OrdenCompraDetalleDTO mapOrdenCompraDetalleToDTO(OrdenCompraDetalle entity) {
        if (entity == null) {
            return null;
        }
        OrdenCompraDetalleDTO dto = new OrdenCompraDetalleDTO();
        dto.setId(entity.getId());
        dto.setCantidad(entity.getCantidad());
        dto.setSubtotal(entity.getSubtotal());
        dto.setPrecioUnitario(entity.getPrecioUnitario());

        if (entity.getOrdenCompra() != null) {
            dto.setOrdenCompraId(entity.getOrdenCompra().getId());
        }
        if (entity.getProductoDetalle() != null) {
            ProductoDetalle pd = entity.getProductoDetalle();
            dto.setProductoDetalleId(pd.getId());

            OrdenCompraDetalleDTO.ProductoDetalleNestedDTO nestedDTO = new OrdenCompraDetalleDTO.ProductoDetalleNestedDTO();
            nestedDTO.setId(pd.getId());
            nestedDTO.setPrecioCompra(pd.getPrecioCompra());
            nestedDTO.setStockActual(pd.getStockActual());
            nestedDTO.setStockMaximo(pd.getStockMaximo());

            nestedDTO.setColor(pd.getColor() != null ? pd.getColor().name() : null);
            nestedDTO.setTalle(pd.getTalle() != null ? pd.getTalle().name() : null);

            if (pd.getProducto() != null) {
                nestedDTO.setProductoDenominacion(pd.getProducto().getDenominacion());
            }
            dto.setProductoDetalle(nestedDTO);
        }
        return dto;
    }

    @Transactional // Este método puede ser llamado dentro de una transacción mayor o iniciar una propia
    public OrdenCompraDetalle mapDTOToOrdenCompraDetalle(OrdenCompraDetalleDTO dto) {
        if (dto == null) {
            return null;
        }
        OrdenCompraDetalle entity = new OrdenCompraDetalle();
        if (dto.getId() != null) {
            entity = ordenCompraDetalleRepository.findById(dto.getId())
                    .orElse(new OrdenCompraDetalle());
            entity.setId(dto.getId());
        }
        entity.setCantidad(dto.getCantidad());
        entity.setSubtotal(dto.getSubtotal());
        entity.setPrecioUnitario(dto.getPrecioUnitario());
        entity.setActivo(true);

        if (dto.getOrdenCompraId() != null) {
            // Asume que la OrdenCompra se seteará desde el servicio padre (OrdenCompraService)
            // o que este DTO es para actualizar un detalle que ya está asociado.
        }

        if (dto.getProductoDetalleId() != null) {
            ProductoDetalle productoDetalle = productoDetalleRepository.findById(dto.getProductoDetalleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado con ID: " + dto.getProductoDetalleId()));
            entity.setProductoDetalle(productoDetalle);
        }

        return entity;
    }

    // --- AGREGADOS: Métodos CRUD con DTOs para el Controller ---

    @Transactional(readOnly = true)
    public List<OrdenCompraDetalleDTO> findAllDTO() throws Exception {
        return ordenCompraDetalleRepository.findAllByActivoTrue()
                .stream()
                .map(this::mapOrdenCompraDetalleToDTO) // Usa el mapeador de Entidad a DTO
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdenCompraDetalleDTO findByIdDTO(Long id) throws Exception {
        OrdenCompraDetalle entity = ordenCompraDetalleRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrdenCompraDetalle no encontrada o inactiva con ID: " + id));
        return mapOrdenCompraDetalleToDTO(entity); // Usa el mapeador de Entidad a DTO
    }

    @Transactional // Añadida anotación Transactional
    public OrdenCompraDetalle saveOrdenCompraDetalleFromDTO(OrdenCompraDetalleDTO dto) {
        throw new UnsupportedOperationException("La creación de detalles de orden debe realizarse a través del OrdenCompraService.");
        // Si REALMENTE necesitas este método para crear detalles de forma independiente,
        // deberías mapear el DTO a la entidad aquí y guardar la entidad.
        // Pero la lógica de negocio dice que los detalles se crean con la OrdenCompra.
    }

    @Transactional // Añadida anotación Transactional
    public OrdenCompraDetalle updateOrdenCompraDetalleFromDTO(Long id, OrdenCompraDetalleDTO dto) {
        OrdenCompraDetalle entityToUpdate = ordenCompraDetalleRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrdenCompraDetalle no encontrada o inactiva para actualizar con ID: " + id));

        entityToUpdate.setCantidad(dto.getCantidad());
        if (dto.getPrecioUnitario() != null) {
            entityToUpdate.setPrecioUnitario(dto.getPrecioUnitario());
        }

        if (dto.getOrdenCompraId() != null && (entityToUpdate.getOrdenCompra() == null || !dto.getOrdenCompraId().equals(entityToUpdate.getOrdenCompra().getId()))) {
            throw new IllegalArgumentException("No se puede reasignar el padre de un detalle de orden directamente. Use OrdenCompraService.");
        }

        if (dto.getProductoDetalleId() != null && (entityToUpdate.getProductoDetalle() == null || !dto.getProductoDetalleId().equals(entityToUpdate.getProductoDetalle().getId()))) {
            ProductoDetalle newProductoDetalle = productoDetalleRepository.findByIdAndActivoTrue(dto.getProductoDetalleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado o inactivo con ID: " + dto.getProductoDetalleId()));
            entityToUpdate.setProductoDetalle(newProductoDetalle);
            if (dto.getPrecioUnitario() == null) {
                entityToUpdate.setPrecioUnitario(newProductoDetalle.getPrecioCompra());
            }
        } else if (dto.getProductoDetalleId() == null && entityToUpdate.getProductoDetalle() != null) {
            entityToUpdate.setProductoDetalle(null);
        }

        return ordenCompraDetalleRepository.save(entityToUpdate);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDetalleDTO> findByOrdenCompraId(Long ordenId) throws Exception {
        List<OrdenCompraDetalle> entities = ordenCompraDetalleRepository.findByOrdenCompraIdAndActivoTrue(ordenId);
        return entities.stream().map(this::mapOrdenCompraDetalleToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDetalleDTO> findByProductoDetalleId(Long productoDetalleId) throws Exception {
        List<OrdenCompraDetalle> entities = ordenCompraDetalleRepository.findByProductoDetalleIdAndActivoTrue(productoDetalleId);
        return entities.stream().map(this::mapOrdenCompraDetalleToDTO).collect(Collectors.toList());
    }
}