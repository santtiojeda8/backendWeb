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
import com.ecommerce.ecommerce.dto.DireccionDTO;
import com.ecommerce.ecommerce.dto.LocalidadDTO;
import com.ecommerce.ecommerce.dto.OrdenCompraDTO;
import com.ecommerce.ecommerce.dto.OrdenCompraDetalleDTO;
import com.ecommerce.ecommerce.dto.ProvinciaDTO;
import com.ecommerce.ecommerce.dto.UserDTO;

import com.ecommerce.ecommerce.exception.ResourceNotFoundException;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrdenCompraService extends BaseService<OrdenCompra, Long> {

    private static final Logger logger = LoggerFactory.getLogger(OrdenCompraService.class);

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 5000;

    private final OrdenCompraRepository ordenCompraRepository;
    private final OrdenCompraDetalleRepository ordenCompraDetalleRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoDetalleRepository productoDetalleRepository;
    private final DireccionRepository direccionRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;
    private final OrdenCompraDetalleService ordenCompraDetalleService;
    private final PaymentClient paymentClient;

    @Autowired
    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository,
                              OrdenCompraDetalleRepository ordenCompraDetalleRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoDetalleRepository productoDetalleRepository,
                              DireccionRepository direccionRepository,
                              LocalidadRepository localidadRepository,
                              ProvinciaRepository provinciaRepository,
                              OrdenCompraDetalleService ordenCompraDetalleService,
                              PaymentClient paymentClient) {
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoDetalleRepository = productoDetalleRepository;
        this.direccionRepository = direccionRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.ordenCompraDetalleService = ordenCompraDetalleService;
        this.paymentClient = paymentClient;
    }

    // --- Métodos de Mapeo de Entidad a DTO ---
    public OrdenCompraDTO mapOrdenCompraToDTO(OrdenCompra ordenCompra) {
        if (ordenCompra == null) {
            return null;
        }

        List<OrdenCompraDetalleDTO> detalleDTOs = null;
        if (ordenCompra.getDetalles() != null) {
            detalleDTOs = ordenCompra.getDetalles().stream()
                    .map(ordenCompraDetalleService::mapOrdenCompraDetalleToDTO)
                    .collect(Collectors.toList());
        }

        UserDTO usuarioDTO = null;
        Long usuarioIdFromEntity = null;
        if (ordenCompra.getUsuario() != null) {
            usuarioDTO = mapUsuarioToUserDTO(ordenCompra.getUsuario());
            usuarioIdFromEntity = ordenCompra.getUsuario().getId();
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

    // Helper method for mapping Usuario to UserDTO
    private UserDTO mapUsuarioToUserDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return UserDTO.builder()
                .id(usuario.getId())
                .firstname(usuario.getNombre())
                .lastname(usuario.getApellido())
                .email(usuario.getEmail())
                .username(usuario.getUsername())
                .build();
    }

    // --- Helper method for mapping Direccion Entity to DireccionDTO ---
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

    // --- Helper method for mapping Localidad Entity to LocalidadDTO ---
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

    // --- Helper method for mapping Provincia Entity to ProvinciaDTO ---
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

    @Transactional
    public OrdenCompra saveOrdenCompraFromDTO(OrdenCompraDTO dto) throws Exception {
        OrdenCompra newOrden = new OrdenCompra();
        newOrden.setFechaCompra(LocalDateTime.now());
        newOrden.setFechaActualizacionEstado(LocalDateTime.now());
        newOrden.setEstadoOrden(EstadoOrdenCompra.PENDIENTE_PAGO); // Initial state
        newOrden.setActivo(true);

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUsuarioId()));
            newOrden.setUsuario(usuario);
        } else {
            throw new IllegalArgumentException("ID de usuario es obligatorio para crear una orden de compra.");
        }

        // Handle shipping details and address
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
            newOrden.setDireccion(direccionRepository.save(newDireccionEntity));
        }

        // Calculate total from details and set it
        BigDecimal calculatedTotal = BigDecimal.ZERO;
        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            for (OrdenCompraDetalleDTO detalleDTO : dto.getDetalles()) {
                ProductoDetalle productoDetalle = productoDetalleRepository.findById(detalleDTO.getProductoDetalleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado con ID: " + detalleDTO.getProductoDetalleId()));

                if (productoDetalle.getStockActual() < detalleDTO.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto: " + productoDetalle.getProducto().getDenominacion());
                }

                OrdenCompraDetalle detalle = ordenCompraDetalleService.mapDTOToOrdenCompraDetalle(detalleDTO);
                detalle.setOrdenCompra(newOrden);
                // Subtotal will be calculated by @PrePersist in OrdenCompraDetalle

                // Add to order details list
                newOrden.addDetalle(detalle);

                calculatedTotal = calculatedTotal.add(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())));

                // Decrease stock
                productoDetalle.setStockActual(productoDetalle.getStockActual() - detalleDTO.getCantidad());
                productoDetalleRepository.save(productoDetalle);
            }
        } else {
            throw new IllegalArgumentException("Una orden de compra debe tener al menos un detalle.");
        }

        // Add shipping cost to total if applicable
        if (newOrden.getCostoEnvio() != null) {
            calculatedTotal = calculatedTotal.add(newOrden.getCostoEnvio());
        }
        newOrden.setTotal(calculatedTotal);

        return ordenCompraRepository.save(newOrden);
    }

    @Transactional
    public OrdenCompra updateOrdenCompraFromDTO(Long id, OrdenCompraDTO dto) throws Exception {
        OrdenCompra entityToUpdate = super.buscarPorId(id);
        if (entityToUpdate == null) {
            throw new ResourceNotFoundException("Orden de compra no encontrada para actualizar con ID: " + id);
        }

        // Update fields from DTO
        entityToUpdate.setTotal(dto.getTotal() != null ? dto.getTotal() : entityToUpdate.getTotal());
        entityToUpdate.setDireccionEnvio(dto.getDireccionEnvio() != null ? dto.getDireccionEnvio() : entityToUpdate.getDireccionEnvio());
        entityToUpdate.setTipoEnvio(dto.getShippingOption() != null ? dto.getShippingOption() : entityToUpdate.getTipoEnvio());
        entityToUpdate.setCostoEnvio(dto.getShippingCost() != null ? dto.getShippingCost() : entityToUpdate.getCostoEnvio());
        entityToUpdate.setTelefono(dto.getBuyerPhoneNumber() != null ? dto.getBuyerPhoneNumber() : entityToUpdate.getTelefono());

        // Update state if provided and different
        if (dto.getEstadoOrden() != null && !entityToUpdate.getEstadoOrden().name().equalsIgnoreCase(dto.getEstadoOrden())) {
            entityToUpdate.setEstadoOrden(EstadoOrdenCompra.valueOf(dto.getEstadoOrden().toUpperCase()));
            entityToUpdate.setFechaActualizacionEstado(LocalDateTime.now());
        }

        // Update user if provided (careful: this changes the owner of the order)
        if (dto.getUsuarioId() != null && (entityToUpdate.getUsuario() == null || !dto.getUsuarioId().equals(entityToUpdate.getUsuario().getId()))) {
            Usuario newUsuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUsuarioId()));
            entityToUpdate.setUsuario(newUsuario);
        }

        // Update address
        if (dto.getDireccionId() != null && (entityToUpdate.getDireccion() == null || !dto.getDireccionId().equals(entityToUpdate.getDireccion().getId()))) {
            Direccion newDireccion = direccionRepository.findByIdAndActivoTrue(dto.getDireccionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dirección existente no encontrada o inactiva con ID: " + dto.getDireccionId()));
            entityToUpdate.setDireccion(newDireccion);
        } else if (dto.getNuevaDireccion() != null) {
            Direccion newDireccionEntity = mapDireccionDTOToEntity(dto.getNuevaDireccion());
            entityToUpdate.setDireccion(direccionRepository.save(newDireccionEntity));
        }

        // Update details
        if (dto.getDetalles() != null) {
            Set<Long> incomingDetailIds = dto.getDetalles().stream()
                    .map(OrdenCompraDetalleDTO::getId)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());

            // Remove details that are no longer present
            entityToUpdate.getDetalles().removeIf(existingDetail ->
                    !incomingDetailIds.contains(existingDetail.getId()));

            // Update or add new details
            for (OrdenCompraDetalleDTO detalleDTO : dto.getDetalles()) {
                if (detalleDTO.getId() != null) {
                    Optional<OrdenCompraDetalle> existingDetailOpt = entityToUpdate.getDetalles().stream()
                            .filter(d -> d.getId().equals(detalleDTO.getId()))
                            .findFirst();

                    if (existingDetailOpt.isPresent()) {
                        OrdenCompraDetalle existingDetail = existingDetailOpt.get();
                        existingDetail.setCantidad(detalleDTO.getCantidad());
                        existingDetail.setPrecioUnitario(detalleDTO.getPrecioUnitario());
                    } else {
                        OrdenCompraDetalle newDetail = ordenCompraDetalleService.mapDTOToOrdenCompraDetalle(detalleDTO);
                        newDetail.setOrdenCompra(entityToUpdate);
                        entityToUpdate.addDetalle(newDetail);
                    }
                } else {
                    OrdenCompraDetalle newDetail = ordenCompraDetalleService.mapDTOToOrdenCompraDetalle(detalleDTO);
                    newDetail.setOrdenCompra(entityToUpdate);
                    entityToUpdate.addDetalle(newDetail);
                }
            }
        } else if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            entityToUpdate.getDetalles().clear();
        }

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


    @Transactional
    public OrdenCompra crearOrdenInicial(
            Long userId,
            String direccionEnvio,
            String buyerPhoneNumber,
            DireccionDTO nuevaDireccionDTO,
            Long direccionIdExistente,
            String shippingOption,
            BigDecimal shippingCost,
            BigDecimal montoTotalCalculado, // <--- Este es el total definitivo calculado en MercadoPagoService
            List<OrdenCompraDetalleDTO> detallesDTO) throws Exception {

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        Direccion direccionAsociada = null;
        if ("delivery".equalsIgnoreCase(shippingOption)) {
            if (direccionIdExistente != null) {
                direccionAsociada = direccionRepository.findByIdAndActivoTrue(direccionIdExistente)
                        .orElseThrow(() -> new ResourceNotFoundException("Direccion existente no encontrada o inactiva con ID: " + direccionIdExistente));
                if (!usuario.getDirecciones().contains(direccionAsociada)) {
                    usuario.addDireccion(direccionAsociada);
                    usuarioRepository.save(usuario);
                }
            } else if (nuevaDireccionDTO != null) {
                Direccion nuevaDireccionEntity = mapDireccionDTOToEntity(nuevaDireccionDTO);
                nuevaDireccionEntity.setActivo(true);
                direccionAsociada = direccionRepository.save(nuevaDireccionEntity);

                usuario.addDireccion(direccionAsociada);
                usuarioRepository.save(usuario);
            } else {
                throw new IllegalArgumentException("Debe proporcionar una dirección existente (direccionId) o una nueva (nuevaDireccion) para envío a domicilio.");
            }
        }

        OrdenCompra ordenCompra = new OrdenCompra();
        ordenCompra.setFechaCompra(LocalDateTime.now());
        ordenCompra.setFechaActualizacionEstado(LocalDateTime.now());
        ordenCompra.setEstadoOrden(EstadoOrdenCompra.PENDIENTE_PAGO);
        ordenCompra.setUsuario(usuario);
        ordenCompra.setDireccionEnvio(direccionEnvio);
        ordenCompra.setTelefono(buyerPhoneNumber);
        ordenCompra.setTipoEnvio(shippingOption);
        ordenCompra.setCostoEnvio(shippingCost);
        ordenCompra.setTotal(montoTotalCalculado); // <--- ¡¡¡Usar directamente el total calculado y enviado desde MercadoPagoService!!!
        ordenCompra.setDireccion(direccionAsociada);
        ordenCompra.setActivo(true);

        // --- Handle Order Details and Stock Management ---
        // Este bucle ahora solo maneja los detalles y el stock, NO recalcula el total de la orden.
        for (OrdenCompraDetalleDTO detalleDTO : detallesDTO) {
            ProductoDetalle productoDetalle = productoDetalleRepository.findById(detalleDTO.getProductoDetalleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto Detalle no encontrado con ID: " + detalleDTO.getProductoDetalleId()));

            if (productoDetalle.getStockActual() < detalleDTO.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + productoDetalle.getProducto().getDenominacion() + ". Stock actual: " + productoDetalle.getStockActual() + ", Cantidad solicitada: " + detalleDTO.getCantidad());
            }

            OrdenCompraDetalle detalle = ordenCompraDetalleService.mapDTOToOrdenCompraDetalle(detalleDTO);
            detalle.setOrdenCompra(ordenCompra);

            ordenCompra.addDetalle(detalle);

            // No se suma al total de la orden aquí, porque ya viene calculado
            // calculatedTotalFromDetails = calculatedTotalFromDetails.add(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())));

            productoDetalle.setStockActual(productoDetalle.getStockActual() - detalleDTO.getCantidad());
            productoDetalleRepository.save(productoDetalle);
        }

        // Ya no es necesario recalcular el total aquí, ya que montoTotalCalculado ya lo incluye
        // if (ordenCompra.getCostoEnvio() != null) {
        //     calculatedTotalFromDetails = calculatedTotalFromDetails.add(ordenCompra.getCostoEnvio());
        // }
        // ordenCompra.setTotal(calculatedTotalFromDetails); // <-- ELIMINAR ESTA LÍNEA

        return ordenCompraRepository.save(ordenCompra);
    }

    @Transactional
    public OrdenCompra actualizarEstadoOrden(Long ordenId, EstadoOrdenCompra nuevoEstado, String mpPaymentId) throws Exception {
        OrdenCompra orden = super.buscarPorId(ordenId);
        if (orden == null) {
            throw new ResourceNotFoundException("Orden de compra no encontrada con ID: " + ordenId);
        }
        orden.setEstadoOrden(nuevoEstado);
        orden.setFechaActualizacionEstado(LocalDateTime.now());
        if (mpPaymentId != null && !mpPaymentId.isEmpty()) {
            orden.setMercadopagoPaymentId(mpPaymentId);
        }
        return ordenCompraRepository.save(orden);
    }

    // --- MÉTODO: Procesar Notificación de Pago de Mercado Pago (Webhook) ---
    @Transactional
    public void procesarNotificacionPagoMP(String paymentId) throws MPException {
        logger.info("Iniciando procesamiento de notificación de pago de Mercado Pago para paymentId: {}", paymentId);

        if (paymentId == null || paymentId.isEmpty()) {
            throw new IllegalArgumentException("El ID de pago de Mercado Pago no puede ser nulo o vacío.");
        }

        Payment payment = null;
        int retries = 0;
        boolean paymentFound = false;

        while (retries < MAX_RETRIES && !paymentFound) {
            try {
                payment = this.paymentClient.get(Long.valueOf(paymentId));
                paymentFound = true;
                logger.info("Pago {} encontrado en Mercado Pago. Estado: {}", paymentId, payment.getStatus());
            } catch (MPApiException e) {
                if (e.getStatusCode() == 404) {
                    retries++;
                    logger.warn("Error 404 (Payment not found) al consultar pago {} en Mercado Pago. Reintento {} de {}. Detalles: {}",
                            paymentId, retries, MAX_RETRIES, e.getApiResponse() != null ? e.getApiResponse().getContent() : "N/A");
                    if (retries < MAX_RETRIES) {
                        try {
                            Thread.sleep(RETRY_DELAY_MS);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new MPException("Interrupción durante el reintento de consulta de pago.", ie);
                        }
                    }
                } else {
                    logger.error("Error al consultar la API de Mercado Pago para el pago {}: {}", paymentId, e.getApiResponse() != null ? e.getApiResponse().getContent() : e.getMessage());
                    throw new MPException("Error al consultar estado del pago en Mercado Pago.", e);
                }
            } catch (Exception e) {
                logger.error("Error inesperado al consultar pago {} en Mercado Pago: {}", paymentId, e.getMessage(), e);
                throw new MPException("Error al consultar estado del pago en Mercado Pago.", e);
            }
        }

        if (!paymentFound || payment == null) {
            logger.error("No se pudo obtener el pago {} de Mercado Pago después de {} reintentos.", paymentId, MAX_RETRIES);
            throw new MPException("No se pudo obtener el pago " + paymentId + " de Mercado Pago después de " + MAX_RETRIES + " reintentos.");
        }

        logger.info("Estado del pago {} en Mercado Pago: {}", paymentId, payment.getStatus());
        logger.info("External Reference asociado al pago {}: {}", paymentId, payment.getExternalReference());

        // Obtener la OrdenCompra usando el external_reference
        Long ordenCompraId;
        try {
            ordenCompraId = Long.valueOf(payment.getExternalReference());
        } catch (NumberFormatException e) {
            logger.error("External Reference '{}' no es un ID numérico válido para la OrdenCompra.", payment.getExternalReference());
            throw new IllegalArgumentException("External Reference no es un ID de OrdenCompra válido.");
        }

        OrdenCompra orden = ordenCompraRepository.findById(ordenCompraId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada con ID: " + ordenCompraId));

        // Actualizar el estado de la orden según el estado del pago de Mercado Pago
        String mpStatus = payment.getStatus();
        EstadoOrdenCompra nuevoEstado;

        switch (mpStatus) {
            case "approved":
                nuevoEstado = EstadoOrdenCompra.PAGADA;
                break;
            case "pending":
            case "in_process":
                nuevoEstado = EstadoOrdenCompra.PENDIENTE_PAGO;
                break;
            case "rejected":
            case "cancelled":
            case "refunded":
            case "charged_back":
                nuevoEstado = EstadoOrdenCompra.RECHAZADA;
                break;
            default:
                logger.warn("Estado de pago desconocido de Mercado Pago para el pago {}: {}", paymentId, mpStatus);
                nuevoEstado = orden.getEstadoOrden(); // Mantener el estado actual si es desconocido
                break;
        }

        // Only update if the state is different or if it's a "more final" state
        if (!orden.getEstadoOrden().equals(nuevoEstado)) {
            orden.setEstadoOrden(nuevoEstado);
            orden.setMercadopagoPaymentId(paymentId); // Save the Mercado Pago payment ID
            ordenCompraRepository.save(orden);
            logger.info("Orden de compra {} actualizada a estado: {}", orden.getId(), nuevoEstado);

            // Additional logic based on the final payment status
            if (nuevoEstado == EstadoOrdenCompra.RECHAZADA) {
                logger.warn("El pago de la orden {} fue RECHAZADO. Revertiendo stock si se había descontado.", orden.getId());
                // Assuming stock was decreased when the order was created
                for (OrdenCompraDetalle detalle : orden.getDetalles()) {
                    ProductoDetalle productoDetalle = detalle.getProductoDetalle();
                    // Revert stock (add back what was deducted)
                    productoDetalle.setStockActual(productoDetalle.getStockActual() + detalle.getCantidad());
                    productoDetalleRepository.save(productoDetalle);
                }
            }
        }
    }
}