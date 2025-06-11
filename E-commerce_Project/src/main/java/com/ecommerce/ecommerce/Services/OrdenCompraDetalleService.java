package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Repositories.OrdenCompraDetalleRepository;
import com.ecommerce.ecommerce.Repositories.ProductoDetalleRepository;
import com.ecommerce.ecommerce.dto.OrdenCompraDetalleDTO;
import com.ecommerce.ecommerce.dto.CreateOrdenCompraDetalleDTO;

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
    private final ProductoDetalleService productoDetalleService; // Considera si realmente lo necesitas aquí si solo usas el repository
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

            // Asume que Color y Talle tienen un método .getNombre() o similar.
            // Si son enums, podrías usar .name() o .toString().
            nestedDTO.setColor(pd.getColor() != null ? pd.getColor().getNombreColor() : null);
            nestedDTO.setTalle(pd.getTalle() != null ? pd.getTalle().getNombreTalle() : null);

            if (pd.getProducto() != null) {
                nestedDTO.setProductoDenominacion(pd.getProducto().getDenominacion());
            }
            dto.setProductoDetalle(nestedDTO);
        }
        return dto;
    }

    @Transactional
    public OrdenCompraDetalle mapDTOToOrdenCompraDetalle(OrdenCompraDetalleDTO dto) {
        if (dto == null) {
            return null;
        }
        OrdenCompraDetalle entity = new OrdenCompraDetalle();
        // Si el DTO trae ID, se intenta cargar para actualizar
        if (dto.getId() != null) {
            // Se busca la entidad existente, si no existe, se crea una nueva (para el caso de que sea un detalle nuevo con ID)
            // Sin embargo, para updates, usualmente primero buscas la entidad padre y luego actualizas los detalles.
            // Esta lógica de `orElse(new OrdenCompraDetalle())` puede ser confusa si no se maneja bien en el padre.
            // Para la creación inicial, es más común que no haya ID de detalle.
            Optional<OrdenCompraDetalle> existingEntityOpt = ordenCompraDetalleRepository.findById(dto.getId());
            if (existingEntityOpt.isPresent()) {
                entity = existingEntityOpt.get();
            } else {
                entity.setId(dto.getId()); // Establece el ID si es un nuevo detalle con ID
            }
        }

        entity.setCantidad(dto.getCantidad());
        entity.setPrecioUnitario(dto.getPrecioUnitario()); // Este DTO sí trae precio unitario
        entity.setActivo(true);

        // Se carga el ProductoDetalle si viene el ID
        if (dto.getProductoDetalleId() != null) {
            ProductoDetalle productoDetalle = productoDetalleRepository.findById(dto.getProductoDetalleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado con ID: " + dto.getProductoDetalleId()));
            entity.setProductoDetalle(productoDetalle);
        } else {
            throw new IllegalArgumentException("Producto Detalle ID es requerido para mapear OrdenCompraDetalleDTO a entidad.");
        }
        // El subtotal se calculará en el @PrePersist/@PreUpdate de la entidad OrdenCompraDetalle
        return entity;
    }

    // ****** ¡EL MÉTODO CRÍTICO CON LA CORRECCIÓN! ******
    @Transactional
    public OrdenCompraDetalle mapCreateDTOToOrdenCompraDetalle(CreateOrdenCompraDetalleDTO dto) {
        if (dto == null) {
            return null;
        }
        OrdenCompraDetalle entity = new OrdenCompraDetalle();
        entity.setCantidad(dto.getCantidad());
        entity.setActivo(true);

        // *** CORRECCIÓN CLAVE: OBTENER Y ASIGNAR PRODUCTODETALLE Y PRECIO ***
        if (dto.getProductoDetalleId() != null) {
            ProductoDetalle productoDetalle = productoDetalleRepository.findById(dto.getProductoDetalleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado con ID: " + dto.getProductoDetalleId()));
            entity.setProductoDetalle(productoDetalle);

            // Asegura que el precio unitario se establezca.
            // Primero intenta tomarlo del DTO si está presente (lo cual es ideal si el front lo calcula),
            // de lo contrario, tómalo del precio de compra/venta del ProductoDetalle.
            if (dto.getPrecioUnitario() != null) {
                entity.setPrecioUnitario(dto.getPrecioUnitario());
            } else {
                // Si el DTO no trae precio unitario, usa el precio del producto detalle
                // Asegúrate de que 'getPrecioVenta()' o 'getPrecioCompra()' sea el precio correcto para la venta.
                entity.setPrecioUnitario(productoDetalle.getPrecioCompra()); // O .getPrecioVenta() si existe
            }
        } else {
            throw new IllegalArgumentException("Producto Detalle ID es requerido para mapear CreateOrdenCompraDetalleDTO a entidad.");
        }

        // El subtotal se calculará automáticamente en el @PrePersist de OrdenCompraDetalle.
        return entity;
    }
    // *******************************************************************

    // --- AGREGADOS: Métodos CRUD con DTOs para el Controller ---

    @Transactional(readOnly = true)
    public List<OrdenCompraDetalleDTO> findAllDTO() throws Exception {
        return ordenCompraDetalleRepository.findAllByActivoTrue()
                .stream()
                .map(this::mapOrdenCompraDetalleToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdenCompraDetalleDTO findByIdDTO(Long id) throws Exception {
        OrdenCompraDetalle entity = ordenCompraDetalleRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrdenCompraDetalle no encontrada o inactiva con ID: " + id));
        return mapOrdenCompraDetalleToDTO(entity);
    }

    @Transactional
    public OrdenCompraDetalle saveOrdenCompraDetalleFromDTO(OrdenCompraDetalleDTO dto) {
        throw new UnsupportedOperationException("La creación de detalles de orden debe realizarse a través del OrdenCompraService.");
    }

    @Transactional
    public OrdenCompraDetalle updateOrdenCompraDetalleFromDTO(Long id, OrdenCompraDetalleDTO dto) {
        OrdenCompraDetalle entityToUpdate = ordenCompraDetalleRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrdenCompraDetalle no encontrada o inactiva para actualizar con ID: " + id));

        entityToUpdate.setCantidad(dto.getCantidad());
        if (dto.getPrecioUnitario() != null) {
            entityToUpdate.setPrecioUnitario(dto.getPrecioUnitario());
        }

        // La reasignación de padre es un caso de uso muy específico y rara vez se permite directamente.
        // Aquí se lanza una excepción para evitar inconsistencias.
        if (dto.getOrdenCompraId() != null && (entityToUpdate.getOrdenCompra() == null || !dto.getOrdenCompraId().equals(entityToUpdate.getOrdenCompra().getId()))) {
            throw new IllegalArgumentException("No se puede reasignar la OrdenCompra padre de un detalle directamente. Use OrdenCompraService para gestionar la relación.");
        }

        if (dto.getProductoDetalleId() != null && (entityToUpdate.getProductoDetalle() == null || !dto.getProductoDetalleId().equals(entityToUpdate.getProductoDetalle().getId()))) {
            ProductoDetalle newProductoDetalle = productoDetalleRepository.findByIdAndActivoTrue(dto.getProductoDetalleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado o inactivo con ID: " + dto.getProductoDetalleId()));
            entityToUpdate.setProductoDetalle(newProductoDetalle);
            // Si el precio unitario no viene en el DTO, lo toma del nuevo producto detalle.
            if (dto.getPrecioUnitario() == null) {
                entityToUpdate.setPrecioUnitario(newProductoDetalle.getPrecioCompra()); // O .getPrecioVenta()
            }
        } else if (dto.getProductoDetalleId() == null && entityToUpdate.getProductoDetalle() != null) {
            // Si el DTO no tiene productoDetalleId pero la entidad sí, lo desvincula.
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