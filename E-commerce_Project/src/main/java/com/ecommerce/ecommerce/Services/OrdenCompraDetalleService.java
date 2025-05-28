package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Repositories.OrdenCompraDetalleRepository;
import com.ecommerce.ecommerce.dto.OrdenCompraDetalleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdenCompraDetalleService extends BaseService<OrdenCompraDetalle, Long> {

    private final OrdenCompraDetalleRepository ordenCompraDetalleRepository;
    private final ProductoDetalleService productoDetalleService;
    private final OrdenCompraService ordenCompraService;

    @Autowired
    public OrdenCompraDetalleService(OrdenCompraDetalleRepository ordenCompraDetalleRepository,
                                     ProductoDetalleService productoDetalleService,
                                     OrdenCompraService ordenCompraService) {
        super(ordenCompraDetalleRepository);
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
        this.productoDetalleService = productoDetalleService;
        this.ordenCompraService = ordenCompraService;
    }

    // --- Métodos de Mapeo Manual (sin ModelMapper) ---

    public OrdenCompraDetalleDTO mapOrdenCompraDetalleToDTO(OrdenCompraDetalle entity) {
        if (entity == null) {
            return null;
        }
        OrdenCompraDetalleDTO dto = new OrdenCompraDetalleDTO();
        dto.setId(entity.getId());
        dto.setCantidad(entity.getCantidad());
        dto.setSubtotal(entity.getSubtotal());

        if (entity.getOrdenCompra() != null) {
            dto.setOrdenCompraId(entity.getOrdenCompra().getId());
        }
        if (entity.getProductoDetalle() != null) {
            dto.setProductoDetalleId(entity.getProductoDetalle().getId());

            ProductoDetalle pd = entity.getProductoDetalle();
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

    public OrdenCompraDetalle mapDTOToOrdenCompraDetalle(OrdenCompraDetalleDTO dto) throws Exception {
        if (dto == null) {
            return null;
        }
        OrdenCompraDetalle entity = new OrdenCompraDetalle();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setCantidad(dto.getCantidad());
        entity.setSubtotal(dto.getSubtotal());

        if (dto.getOrdenCompraId() != null) {
            // Usar buscarPorId del propio servicio (que hereda de BaseService)
            OrdenCompra ordenCompra = ordenCompraService.buscarPorId(dto.getOrdenCompraId());
            entity.setOrdenCompra(ordenCompra);
        }
        if (dto.getProductoDetalleId() != null) {
            // Usar buscarPorId del propio servicio (que hereda de BaseService)
            ProductoDetalle productoDetalle = productoDetalleService.buscarPorId(dto.getProductoDetalleId());
            entity.setProductoDetalle(productoDetalle);
        }
        return entity;
    }

    // --- Métodos CRUD con DTOs ---

    public List<OrdenCompraDetalleDTO> findAllDTO() throws Exception {
        return ordenCompraDetalleRepository.findAllByActivoTrue()
                .stream()
                .map(this::mapOrdenCompraDetalleToDTO)
                .collect(Collectors.toList());
    }

    public OrdenCompraDetalleDTO findByIdDTO(Long id) throws Exception {
        // Usar buscarPorId del propio servicio (que hereda de BaseService)
        OrdenCompraDetalle entity = super.buscarPorId(id);
        return mapOrdenCompraDetalleToDTO(entity);
    }

    public OrdenCompraDetalle saveOrdenCompraDetalleFromDTO(OrdenCompraDetalleDTO dto) throws Exception {
        OrdenCompraDetalle entity = new OrdenCompraDetalle();
        entity.setCantidad(dto.getCantidad());

        if (dto.getProductoDetalleId() != null) {
            // Usar buscarPorId del propio servicio (que hereda de BaseService)
            ProductoDetalle productoDetalle = productoDetalleService.buscarPorId(dto.getProductoDetalleId());
            entity.setProductoDetalle(productoDetalle);
            entity.setSubtotal(entity.getCantidad() * productoDetalle.getPrecioCompra());
        } else {
            throw new Exception("Se requiere el ID del ProductoDetalle para crear un detalle de orden de compra.");
        }

        if (dto.getOrdenCompraId() != null) {
            // Usar buscarPorId del propio servicio (que hereda de BaseService)
            OrdenCompra ordenCompra = ordenCompraService.buscarPorId(dto.getOrdenCompraId());
            entity.setOrdenCompra(ordenCompra);
        } else {
            throw new Exception("Se requiere el ID de la Orden de Compra para crear un detalle de orden de compra.");
        }

        entity.setActivo(true);
        return ordenCompraDetalleRepository.save(entity);
    }

    public OrdenCompraDetalle updateOrdenCompraDetalleFromDTO(Long id, OrdenCompraDetalleDTO dto) throws Exception {
        // Usar buscarPorId del propio servicio (que hereda de BaseService)
        OrdenCompraDetalle entityToUpdate = super.buscarPorId(id);

        entityToUpdate.setCantidad(dto.getCantidad());

        if (dto.getOrdenCompraId() != null && (entityToUpdate.getOrdenCompra() == null || !dto.getOrdenCompraId().equals(entityToUpdate.getOrdenCompra().getId()))) {
            // Usar buscarPorId del propio servicio (que hereda de BaseService)
            OrdenCompra newOrdenCompra = ordenCompraService.buscarPorId(dto.getOrdenCompraId());
            entityToUpdate.setOrdenCompra(newOrdenCompra);
        } else if (dto.getOrdenCompraId() == null && entityToUpdate.getOrdenCompra() != null) {
            entityToUpdate.setOrdenCompra(null);
        }

        if (dto.getProductoDetalleId() != null && (entityToUpdate.getProductoDetalle() == null || !dto.getProductoDetalleId().equals(entityToUpdate.getProductoDetalle().getId()))) {
            // Usar buscarPorId del propio servicio (que hereda de BaseService)
            ProductoDetalle newProductoDetalle = productoDetalleService.buscarPorId(dto.getProductoDetalleId());
            entityToUpdate.setProductoDetalle(newProductoDetalle);
        } else if (dto.getProductoDetalleId() == null && entityToUpdate.getProductoDetalle() != null) {
            entityToUpdate.setProductoDetalle(null);
        }

        if (entityToUpdate.getProductoDetalle() != null) {
            entityToUpdate.setSubtotal(entityToUpdate.getCantidad() * entityToUpdate.getProductoDetalle().getPrecioCompra());
        }

        return ordenCompraDetalleRepository.save(entityToUpdate);
    }

    // --- Métodos Específicos para el Controlador (Devuelven Listas de DTOs) ---

    public List<OrdenCompraDetalleDTO> findByOrdenCompraId(Long ordenId) throws Exception {
        List<OrdenCompraDetalle> entities = ordenCompraDetalleRepository.findByOrdenCompraIdAndActivoTrue(ordenId);
        return entities.stream()
                .map(this::mapOrdenCompraDetalleToDTO)
                .collect(Collectors.toList());
    }

    public List<OrdenCompraDetalleDTO> findByProductoDetalleId(Long productoDetalleId) throws Exception {
        List<OrdenCompraDetalle> entities = ordenCompraDetalleRepository.findByProductoDetalleIdAndActivoTrue(productoDetalleId);
        return entities.stream()
                .map(this::mapOrdenCompraDetalleToDTO)
                .collect(Collectors.toList());
    }

    // El método 'eliminar' se hereda de BaseService y ya debe manejar el soft delete
}