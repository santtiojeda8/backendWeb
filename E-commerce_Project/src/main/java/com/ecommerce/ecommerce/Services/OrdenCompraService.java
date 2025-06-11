package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.Provincia;
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Entities.enums.EstadoOrdenCompra;
import com.ecommerce.ecommerce.Repositories.DireccionRepository;
import com.ecommerce.ecommerce.Repositories.LocalidadRepository;
import com.ecommerce.ecommerce.Repositories.OrdenCompraDetalleRepository;
import com.ecommerce.ecommerce.Repositories.OrdenCompraRepository;
import com.ecommerce.ecommerce.Repositories.ProductoDetalleRepository;
import com.ecommerce.ecommerce.Repositories.ProvinciaRepository;
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import com.ecommerce.ecommerce.dto.CreateOrdenCompraDetalleDTO;
import com.ecommerce.ecommerce.dto.DireccionDTO;
import com.ecommerce.ecommerce.dto.LocalidadDTO;
import com.ecommerce.ecommerce.dto.OrdenCompraDTO;
import com.ecommerce.ecommerce.dto.OrdenCompraDetalleDTO;
import com.ecommerce.ecommerce.dto.ProvinciaDTO;
import com.ecommerce.ecommerce.dto.UserInfoDTO;

import com.ecommerce.ecommerce.exception.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrdenCompraService extends BaseService<OrdenCompra, Long> {

    private static final Logger logger = LoggerFactory.getLogger(OrdenCompraService.class);

    private final OrdenCompraRepository ordenCompraRepository;
    private final OrdenCompraDetalleRepository ordenCompraDetalleRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoDetalleRepository productoDetalleRepository;
    private final DireccionRepository direccionRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;
    private final OrdenCompraDetalleService ordenCompraDetalleService;

    @Autowired
    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository,
                              OrdenCompraDetalleRepository ordenCompraDetalleRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoDetalleRepository productoDetalleRepository,
                              DireccionRepository direccionRepository,
                              LocalidadRepository localidadRepository,
                              ProvinciaRepository provinciaRepository,
                              OrdenCompraDetalleService ordenCompraDetalleService
    ) {
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoDetalleRepository = productoDetalleRepository;
        this.direccionRepository = direccionRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.ordenCompraDetalleService = ordenCompraDetalleService;
    }

    // --- Métodos de Mapeo de Entidad a DTO ---
    public OrdenCompraDTO mapOrdenCompraToDTO(OrdenCompra ordenCompra) {
        if (ordenCompra == null) {
            return null;
        }

        List<OrdenCompraDetalleDTO> detalleDTOs = null;
        // Asegúrate de que la colección de detalles no sea null antes de intentar stream
        // y que la carga lazy de detalles sea manejada si este método se llama en un contexto sin transacción
        if (ordenCompra.getDetalles() != null && !ordenCompra.getDetalles().isEmpty()) {
            detalleDTOs = ordenCompra.getDetalles().stream()
                    .map(ordenCompraDetalleService::mapOrdenCompraDetalleToDTO)
                    .collect(Collectors.toList());
        }

        UserInfoDTO usuarioInfoDTO = null;
        Long usuarioIdFromEntity = null;

        if (ordenCompra.getUsuario() != null) {
            usuarioInfoDTO = mapUsuarioToUserInfoDTO(ordenCompra.getUsuario());
            usuarioIdFromEntity = ordenCompra.getUsuario().getId();
        } else {
            logger.warn("La orden de compra ID {} no tiene un objeto Usuario asociado cargado. UserInfoDTO y usuarioId serán null.", ordenCompra.getId());
        }

        DireccionDTO direccionDTO = null;
        if(ordenCompra.getDireccion() != null){
            direccionDTO = mapDireccionToDTO(ordenCompra.getDireccion());
        }

        return OrdenCompraDTO.builder()
                .id(ordenCompra.getId())
                .total(ordenCompra.getTotal())
                .fechaCompra(ordenCompra.getFechaCompra())
                .direccionEnvio(ordenCompra.getDireccionEnvio())
                .detalles(detalleDTOs)
                .usuarioId(usuarioIdFromEntity)
                .usuarioInfoDTO(usuarioInfoDTO)
                .estadoOrden(ordenCompra.getEstadoOrden() != null ? ordenCompra.getEstadoOrden().name() : null)
                .mercadopagoPreferenceId(ordenCompra.getMercadopagoPreferenceId())
                .mercadopagoPaymentId(ordenCompra.getMercadopagoPaymentId())
                .shippingOption(ordenCompra.getTipoEnvio())
                .shippingCost(ordenCompra.getCostoEnvio())
                .buyerPhoneNumber(ordenCompra.getTelefono())
                .direccionId(ordenCompra.getDireccion() != null ? ordenCompra.getDireccion().getId() : null)
                .nuevaDireccion(direccionDTO)
                .build();
    }

    private UserInfoDTO mapUsuarioToUserInfoDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return UserInfoDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .build();
    }

    private DireccionDTO mapDireccionToDTO(Direccion entity) {
        if (entity == null) return null;
        LocalidadDTO localidadDTO = null;
        if (entity.getLocalidad() != null) {
            localidadDTO = mapLocalidadToDTO(entity.getLocalidad());
        }
        return DireccionDTO.builder()
                .id(entity.getId())
                .calle(entity.getCalle())
                .numero(entity.getNumero())
                .piso(entity.getPiso())
                .departamento(entity.getDepartamento())
                .cp(entity.getCp())
                .localidad(localidadDTO)
                .build();
    }

    private LocalidadDTO mapLocalidadToDTO(Localidad entity) {
        if (entity == null) return null;
        ProvinciaDTO provinciaDTO = null;
        if (entity.getProvincia() != null) {
            provinciaDTO = mapProvinciaToDTO(entity.getProvincia());
        }
        return LocalidadDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .provincia(provinciaDTO)
                .build();
    }

    private ProvinciaDTO mapProvinciaToDTO(Provincia entity) {
        if (entity == null) return null;
        return ProvinciaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }

    private Direccion mapDireccionDTOToEntity(DireccionDTO dto) {
        if (dto == null) {
            return null;
        }

        Direccion direccion = Direccion.builder()
                .id(dto.getId())
                .calle(dto.getCalle())
                .numero(dto.getNumero())
                .piso(dto.getPiso())
                .departamento(dto.getDepartamento())
                .cp(dto.getCp())
                .activo(true)
                .build();

        if (dto.getLocalidad() != null && dto.getLocalidad().getNombre() != null) {
            Localidad localidad = null;

            if (dto.getLocalidad().getId() != null) {
                localidad = localidadRepository.findByIdAndActivoTrue(dto.getLocalidad().getId()).orElse(null);
            }

            if (localidad == null) {
                final Provincia provinciaParaLocalidad;

                if (dto.getLocalidad().getProvincia() != null && dto.getLocalidad().getProvincia().getNombre() != null) {
                    provinciaParaLocalidad = provinciaRepository.findByNombreAndActivoTrue(dto.getLocalidad().getProvincia().getNombre())
                            .orElseGet(() -> {
                                Provincia newProvincia = Provincia.builder()
                                        .nombre(dto.getLocalidad().getProvincia().getNombre())
                                        .activo(true)
                                        .build();
                                return provinciaRepository.save(newProvincia);
                            });
                } else {
                    throw new IllegalArgumentException("Provincia es requerida para la localidad.");
                }

                localidad = localidadRepository.findByNombreAndProvinciaAndActivoTrue(dto.getLocalidad().getNombre(), provinciaParaLocalidad)
                        .orElseGet(() -> {
                            Localidad newLocalidad = Localidad.builder()
                                    .nombre(dto.getLocalidad().getNombre())
                                    .provincia(provinciaParaLocalidad)
                                    .activo(true)
                                    .build();
                            return localidadRepository.save(newLocalidad);
                        });
            }
            direccion.setLocalidad(localidad);
        } else {
            throw new IllegalArgumentException("Localidad es requerida para un domicilio.");
        }
        return direccion;
    }

    // --- Métodos CRUD con DTOs ---

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> findAllDTO() throws Exception {
        return ordenCompraRepository.findAllByActivoTrue()
                .stream()
                .map(this::mapOrdenCompraToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdenCompraDTO findByIdDTO(Long id) throws Exception {
        OrdenCompra entity = super.buscarPorId(id);
        return mapOrdenCompraToDTO(entity);
    }

    // Nota: Este método saveOrdenCompraFromDTO no es el que usa el MercadoPagoController.
    // Si lo usas en otro lugar, asegúrate de que el DTO que le pasas sea adecuado.
    @Transactional
    public OrdenCompra saveOrdenCompraFromDTO(OrdenCompraDTO dto) throws Exception {
        OrdenCompra newOrden = new OrdenCompra();
        newOrden.setFechaCompra(LocalDateTime.now());
        newOrden.setFechaActualizacionEstado(LocalDateTime.now());
        newOrden.setEstadoOrden(EstadoOrdenCompra.PENDIENTE_PAGO); // Estado inicial
        newOrden.setActivo(true);

        Long userIdToUse = (dto.getUsuarioInfoDTO() != null && dto.getUsuarioInfoDTO().getId() != null)
                ? dto.getUsuarioInfoDTO().getId()
                : dto.getUsuarioId();

        if (userIdToUse != null) {
            Usuario usuario = usuarioRepository.findById(userIdToUse)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userIdToUse));
            newOrden.setUsuario(usuario);
        } else {
            throw new IllegalArgumentException("ID de usuario es obligatorio para crear una orden de compra.");
        }

        newOrden.setTipoEnvio(dto.getShippingOption());
        newOrden.setCostoEnvio(dto.getShippingCost());
        newOrden.setTelefono(dto.getBuyerPhoneNumber());
        newOrden.setDireccionEnvio(dto.getDireccionEnvio());

        if (dto.getDireccionId() != null) {
            Direccion existingDireccion = direccionRepository.findByIdAndActivoTrue(dto.getDireccionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dirección existente no encontrada o inactiva con ID: " + dto.getDireccionId()));
            newOrden.setDireccion(existingDireccion);
        } else if (dto.getNuevaDireccion() != null) {
            Direccion newDireccionEntity = mapDireccionDTOToEntity(dto.getNuevaDireccion());
            if (newOrden.getUsuario() != null) {
                newDireccionEntity.setUsuario(newOrden.getUsuario());
            }
            newOrden.setDireccion(direccionRepository.save(newDireccionEntity));
        }

        // Aquí la lógica de detalles debe usar OrdenCompraDetalleDTO si este es el DTO de entrada.
        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            for (OrdenCompraDetalleDTO detalleDTO : dto.getDetalles()) {
                ProductoDetalle productoDetalle = productoDetalleRepository.findById(detalleDTO.getProductoDetalleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado con ID: " + detalleDTO.getProductoDetalleId()));

                if (productoDetalle.getStockActual() < detalleDTO.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto: " + productoDetalle.getProducto().getDenominacion());
                }

                OrdenCompraDetalle detalle = ordenCompraDetalleService.mapDTOToOrdenCompraDetalle(detalleDTO);
                detalle.setOrdenCompra(newOrden);
                detalle.setProductoDetalle(productoDetalle); // Asegura la relación con ProductoDetalle
                detalle.setSubtotal(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()))); // Recalcula subtotal

                newOrden.addDetalle(detalle);

                productoDetalle.setStockActual(productoDetalle.getStockActual() - detalleDTO.getCantidad());
                productoDetalleRepository.save(productoDetalle);
            }
        } else {
            throw new IllegalArgumentException("Una orden de compra debe tener al menos un detalle.");
        }

        // El total se recalculará automáticamente antes de persistir debido a @PrePersist/@PreUpdate
        return ordenCompraRepository.save(newOrden);
    }

    @Transactional
    public OrdenCompra updateOrdenCompraFromDTO(Long id, OrdenCompraDTO dto) throws Exception {
        OrdenCompra entityToUpdate = super.buscarPorId(id);
        if (entityToUpdate == null) {
            throw new ResourceNotFoundException("Orden de compra no encontrada para actualizar con ID: " + id);
        }

        // No actualizar el total directamente si se recalcula en @PrePersist/@PreUpdate
        // entityToUpdate.setTotal(dto.getTotal() != null ? dto.getTotal() : entityToUpdate.getTotal());
        entityToUpdate.setDireccionEnvio(dto.getDireccionEnvio() != null ? dto.getDireccionEnvio() : entityToUpdate.getDireccionEnvio());
        entityToUpdate.setTipoEnvio(dto.getShippingOption() != null ? dto.getShippingOption() : entityToUpdate.getTipoEnvio());
        entityToUpdate.setCostoEnvio(dto.getShippingCost() != null ? dto.getShippingCost() : entityToUpdate.getCostoEnvio());
        entityToUpdate.setTelefono(dto.getBuyerPhoneNumber() != null ? dto.getBuyerPhoneNumber() : entityToUpdate.getTelefono());

        if (dto.getEstadoOrden() != null && !entityToUpdate.getEstadoOrden().name().equalsIgnoreCase(dto.getEstadoOrden())) {
            entityToUpdate.setEstadoOrden(EstadoOrdenCompra.valueOf(dto.getEstadoOrden().toUpperCase()));
            entityToUpdate.setFechaActualizacionEstado(LocalDateTime.now());
        }

        Long userIdToUse = (dto.getUsuarioInfoDTO() != null && dto.getUsuarioInfoDTO().getId() != null)
                ? dto.getUsuarioInfoDTO().getId()
                : dto.getUsuarioId();

        if (userIdToUse != null && (entityToUpdate.getUsuario() == null || !userIdToUse.equals(entityToUpdate.getUsuario().getId()))) {
            Usuario newUsuario = usuarioRepository.findById(userIdToUse)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userIdToUse));
            entityToUpdate.setUsuario(newUsuario);
        }

        if (dto.getDireccionId() != null && (entityToUpdate.getDireccion() == null || !dto.getDireccionId().equals(entityToUpdate.getDireccion().getId()))) {
            Direccion newDireccion = direccionRepository.findByIdAndActivoTrue(dto.getDireccionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dirección existente no encontrada o inactiva con ID: " + dto.getDireccionId()));
            entityToUpdate.setDireccion(newDireccion);
        } else if (dto.getNuevaDireccion() != null) {
            Direccion newDireccionEntity = mapDireccionDTOToEntity(dto.getNuevaDireccion());
            if (entityToUpdate.getUsuario() != null) {
                newDireccionEntity.setUsuario(entityToUpdate.getUsuario());
            }
            entityToUpdate.setDireccion(direccionRepository.save(newDireccionEntity));
        }

        // ⭐ Lógica de actualización de detalles: Limpia los existentes y añade los nuevos.
        // Esto es una estrategia común, pero puede ser ineficiente para muchos detalles.
        // Asegúrate de que los IDs de los detalles se manejen correctamente si se espera actualizar detalles específicos.
        if (dto.getDetalles() != null) {
            // Eliminar detalles que ya no están en el DTO (si tienen ID)
            Set<Long> incomingDetailIds = dto.getDetalles().stream()
                    .map(OrdenCompraDetalleDTO::getId)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());

            // Usamos un iterador para eliminar de forma segura
            entityToUpdate.getDetalles().removeIf(existingDetail ->
                    existingDetail.getId() != null && !incomingDetailIds.contains(existingDetail.getId()));

            for (OrdenCompraDetalleDTO detalleDTO : dto.getDetalles()) {
                if (detalleDTO.getId() != null) {
                    // Detalle existente: buscar y actualizar
                    Optional<OrdenCompraDetalle> existingDetailOpt = entityToUpdate.getDetalles().stream()
                            .filter(d -> d.getId().equals(detalleDTO.getId()))
                            .findFirst();

                    if (existingDetailOpt.isPresent()) {
                        OrdenCompraDetalle existingDetail = existingDetailOpt.get();
                        existingDetail.setCantidad(detalleDTO.getCantidad());
                        existingDetail.setPrecioUnitario(detalleDTO.getPrecioUnitario());
                        // Recalcular subtotal
                        existingDetail.setSubtotal(existingDetail.getPrecioUnitario().multiply(new BigDecimal(existingDetail.getCantidad())));
                    } else {
                        // Detalle con ID pero no presente en la colección actual (podría ser un nuevo detalle con ID preasignado)
                        // Crear y añadir
                        OrdenCompraDetalle newDetail = ordenCompraDetalleService.mapDTOToOrdenCompraDetalle(detalleDTO);
                        newDetail.setOrdenCompra(entityToUpdate);
                        // Asegurar el producto detalle para el nuevo detalle
                        ProductoDetalle productoDetalle = productoDetalleRepository.findById(detalleDTO.getProductoDetalleId())
                                .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado con ID: " + detalleDTO.getProductoDetalleId() + " para nuevo detalle."));
                        newDetail.setProductoDetalle(productoDetalle);
                        newDetail.setSubtotal(newDetail.getPrecioUnitario().multiply(new BigDecimal(newDetail.getCantidad())));
                        entityToUpdate.addDetalle(newDetail);
                    }
                } else {
                    // Nuevo detalle sin ID: crear y añadir
                    OrdenCompraDetalle newDetail = ordenCompraDetalleService.mapDTOToOrdenCompraDetalle(detalleDTO);
                    newDetail.setOrdenCompra(entityToUpdate);
                    // Asegurar el producto detalle para el nuevo detalle
                    ProductoDetalle productoDetalle = productoDetalleRepository.findById(detalleDTO.getProductoDetalleId())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado con ID: " + detalleDTO.getProductoDetalleId() + " para nuevo detalle."));
                    newDetail.setProductoDetalle(productoDetalle);
                    newDetail.setSubtotal(newDetail.getPrecioUnitario().multiply(new BigDecimal(newDetail.getCantidad())));
                    entityToUpdate.addDetalle(newDetail);
                }
            }
        } else if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            // Si el DTO de entrada no tiene detalles o está vacío, se borran todos los detalles existentes
            entityToUpdate.getDetalles().clear();
        }

        // El total se recalculará automáticamente antes de persistir debido a @PrePersist/@PreUpdate
        return ordenCompraRepository.save(entityToUpdate);
    }


    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerPorFecha(LocalDateTime fecha) throws Exception {
        LocalDateTime startOfDay = fecha.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = fecha.toLocalDate().atTime(23, 59, 59, 999_999_999);
        List<OrdenCompra> entities = ordenCompraRepository.findByFechaCompraBetweenAndActivoTrue(startOfDay, endOfDay);
        return entities.stream().map(this::mapOrdenCompraToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerPorUsuarioDTO(Long userId) throws Exception {
        List<OrdenCompra> entities = ordenCompraRepository.findByUsuarioIdAndActivoTrue(userId);
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron órdenes de compra activas para el usuario ID: " + userId);
        }
        return entities.stream().map(this::mapOrdenCompraToDTO).collect(Collectors.toList());
    }

    /**
     * Crea una orden de compra inicial en la base de datos con los datos proporcionados.
     * Esta orden se establece en estado PENDIENTE_PAGO y su total se basa en el monto
     * calculado externamente (ej. por MercadoPagoController antes de crear la preferencia).
     *
     * @param userId             ID del usuario que realiza la compra.
     * @param buyerPhoneNumber   Número de teléfono del comprador.
     * @param nuevaDireccionDTO  DTO con los datos de una nueva dirección (si aplica).
     * @param direccionIdExistente ID de una dirección existente (si aplica).
     * @param shippingOption     Opción de envío ("delivery" o "pickup").
     * @param shippingCost       Costo del envío.
     * @param montoTotalCalculado Monto total final de la orden (incluyendo envío).
     * @param detallesDTO        Lista de detalles de la orden (CreateOrdenCompraDetalleDTO).
     * @return La OrdenCompra creada y persistida.
     * @throws Exception Si el usuario no se encuentra, hay problemas de stock o datos de dirección.
     */
    @Transactional
    public OrdenCompra crearOrdenInicial(
            Long userId,
            String buyerPhoneNumber,
            DireccionDTO nuevaDireccionDTO,
            Long direccionIdExistente,
            String shippingOption,
            BigDecimal shippingCost,
            BigDecimal montoTotalCalculado,
            List<CreateOrdenCompraDetalleDTO> detallesDTO) throws Exception {

        logger.info("Creando orden inicial para usuario ID: {}", userId);

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        Direccion direccionAsociada = null;
        if ("delivery".equalsIgnoreCase(shippingOption)) {
            if (direccionIdExistente != null) {
                direccionAsociada = direccionRepository.findByIdAndActivoTrue(direccionIdExistente)
                        .orElseThrow(() -> new ResourceNotFoundException("Direccion existente no encontrada o inactiva con ID: " + direccionIdExistente));
                // Asegurar que la dirección esté asociada al usuario si no lo está ya
                if (!usuario.getDirecciones().contains(direccionAsociada)) {
                    usuario.addDireccion(direccionAsociada); // Usar el método addDireccion de Usuario
                    usuarioRepository.save(usuario);
                    logger.info("Asociando dirección existente ID {} a usuario ID {}", direccionIdExistente, userId);
                }
            } else if (nuevaDireccionDTO != null) {
                Direccion nuevaDireccionEntity = mapDireccionDTOToEntity(nuevaDireccionDTO);
                nuevaDireccionEntity.setActivo(true);
                nuevaDireccionEntity.setUsuario(usuario);
                direccionAsociada = direccionRepository.save(nuevaDireccionEntity);

                usuario.addDireccion(direccionAsociada); // Usar el método addDireccion de Usuario
                usuarioRepository.save(usuario);
                logger.info("Creando y asociando nueva dirección a usuario ID {}", userId);
            } else {
                throw new IllegalArgumentException("Debe proporcionar una dirección existente (direccionId) o una nueva (nuevaDireccion) para envío a domicilio.");
            }
        }

        OrdenCompra ordenCompra = new OrdenCompra();
        ordenCompra.setFechaCompra(LocalDateTime.now());
        ordenCompra.setFechaActualizacionEstado(LocalDateTime.now());
        ordenCompra.setEstadoOrden(EstadoOrdenCompra.PENDIENTE_PAGO);
        ordenCompra.setUsuario(usuario);
        ordenCompra.setTelefono(buyerPhoneNumber);
        ordenCompra.setTipoEnvio(shippingOption);
        ordenCompra.setCostoEnvio(shippingCost);
        // El total se setea aquí con el monto calculado externamente.
        // Luego, el @PrePersist/@PreUpdate en la entidad recalculará para asegurar consistencia.
        ordenCompra.setTotal(montoTotalCalculado);
        ordenCompra.setDireccion(direccionAsociada);
        ordenCompra.setActivo(true);

        for (CreateOrdenCompraDetalleDTO detalleDTO : detallesDTO) {
            ProductoDetalle productoDetalle = productoDetalleRepository.findById(detalleDTO.getProductoDetalleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado con ID: " + detalleDTO.getProductoDetalleId()));

            if (productoDetalle.getStockActual() < detalleDTO.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + productoDetalle.getProducto().getDenominacion() + ". Stock actual: " + productoDetalle.getStockActual() + ", Cantidad solicitada: " + detalleDTO.getCantidad());
            }

            OrdenCompraDetalle detalle = ordenCompraDetalleService.mapCreateDTOToOrdenCompraDetalle(detalleDTO);
            detalle.setOrdenCompra(ordenCompra); // Vincula el detalle a la orden
            detalle.setProductoDetalle(productoDetalle); // Vincula el detalle al ProductoDetalle
            // Calcula el subtotal aquí. Esto se usará en el recalcularTotal() de la OrdenCompra
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())));

            ordenCompra.addDetalle(detalle); // Añade el detalle a la colección de la orden (usando el método addDetalle)

            // Descuenta el stock
            productoDetalle.setStockActual(productoDetalle.getStockActual() - detalleDTO.getCantidad());
            productoDetalleRepository.save(productoDetalle);
            logger.info("Stock de ProductoDetalle ID {} disminuido en {}. Nuevo stock: {}", productoDetalle.getId(), detalleDTO.getCantidad(), productoDetalle.getStockActual());
        }

        // Al guardar la orden, el @PrePersist/@PreUpdate de la entidad recalculará el total
        // con base en los detalles agregados y el costo de envío.
        OrdenCompra savedOrden = ordenCompraRepository.save(ordenCompra);
        logger.info("Orden de compra ID {} creada exitosamente con estado PENDIENTE_PAGO.", savedOrden.getId());
        return savedOrden;
    }

    @Transactional
    public OrdenCompra actualizarEstadoOrdenYStock(Long ordenId, EstadoOrdenCompra nuevoEstado, String mpPaymentId) throws Exception {
        logger.info("Actualizando estado de orden ID {} a {}. Payment ID: {}", ordenId, nuevoEstado, mpPaymentId);
        OrdenCompra orden = super.buscarPorId(ordenId);
        if (orden == null) {
            throw new ResourceNotFoundException("Orden de compra no encontrada con ID: " + ordenId);
        }

        EstadoOrdenCompra estadoActual = orden.getEstadoOrden();

        if (!estadoActual.equals(nuevoEstado)) {
            orden.setEstadoOrden(nuevoEstado);
            orden.setFechaActualizacionEstado(LocalDateTime.now());
            if (mpPaymentId != null && !mpPaymentId.isEmpty()) {
                orden.setMercadopagoPaymentId(mpPaymentId);
            }

            if (nuevoEstado == EstadoOrdenCompra.PAGADA && estadoActual != EstadoOrdenCompra.PAGADA) {
                // Si la orden pasa a PAGADA, el stock ya se descontó en la creación inicial.
                // No es necesario descontar de nuevo.
                logger.info("Orden {} pasa a PAGADA. Stock ya fue descontado en la creación inicial.", orden.getId());
            } else if (nuevoEstado == EstadoOrdenCompra.RECHAZADA && estadoActual != EstadoOrdenCompra.RECHAZADA) {
                // Si la orden es rechazada, se revierte el stock si se había descontado.
                logger.warn("El pago de la orden {} fue RECHAZADO o similar. Revertiendo stock si se había descontado.", orden.getId());
                // Asegúrate de que los detalles estén cargados (si es LAZY y no se cargó aún)
                if (orden.getDetalles() != null) {
                    for (OrdenCompraDetalle detalle : orden.getDetalles()) {
                        ProductoDetalle productoDetalle = detalle.getProductoDetalle();
                        if (productoDetalle != null) {
                            productoDetalle.setStockActual(productoDetalle.getStockActual() + detalle.getCantidad());
                            productoDetalleRepository.save(productoDetalle);
                            logger.info("Stock de ProductoDetalle ID {} revertido en {}. Nuevo stock: {}",
                                    productoDetalle.getId(), detalle.getCantidad(), productoDetalle.getStockActual());
                        }
                    }
                }
            }
        }
        OrdenCompra updatedOrden = ordenCompraRepository.save(orden);
        logger.info("Orden de compra ID {} actualizada a estado {}.", updatedOrden.getId(), updatedOrden.getEstadoOrden());
        return updatedOrden;
    }
}