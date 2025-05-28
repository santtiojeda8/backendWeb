package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Repositories.OrdenCompraRepository;
import com.ecommerce.ecommerce.Repositories.ProductoDetalleRepository;
import com.ecommerce.ecommerce.Repositories.UsuarioRepository;
import com.ecommerce.ecommerce.dto.OrdenCompraDTO;
import com.ecommerce.ecommerce.dto.OrdenCompraDetalleDTO;
import com.ecommerce.ecommerce.dto.UserDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class OrdenCompraService extends BaseService<OrdenCompra, Long> {

    private final OrdenCompraRepository ordenCompraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoDetalleRepository productoDetalleRepository;

    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoDetalleRepository productoDetalleRepository) {
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoDetalleRepository = productoDetalleRepository;
    }

    // --- Métodos de Mapeo de Entidad a DTO ---
    public OrdenCompraDTO mapOrdenCompraToDTO(OrdenCompra ordenCompra) {
        if (ordenCompra == null) {
            return null;
        }

        List<OrdenCompraDetalleDTO> detalleDTOs = null;
        if (ordenCompra.getDetalles() != null) {
            detalleDTOs = ordenCompra.getDetalles().stream()
                    .map(this::mapOrdenCompraDetalleToDTO)
                    .collect(Collectors.toList());
        }

        UserDTO usuarioDTO = null;
        if (ordenCompra.getUsuario() != null) {
            usuarioDTO = mapUsuarioToUserDTO(ordenCompra.getUsuario());
        }

        return OrdenCompraDTO.builder()
                .id(ordenCompra.getId())
                .total(ordenCompra.getTotal())
                .fechaCompra(ordenCompra.getFechaCompra())
                .direccionEnvio(ordenCompra.getDireccionEnvio())
                .detalles(detalleDTOs)
                .usuario(usuarioDTO)
                .build();
    }

    public OrdenCompraDetalleDTO mapOrdenCompraDetalleToDTO(OrdenCompraDetalle detalle) {
        if (detalle == null) {
            return null;
        }

        OrdenCompraDetalleDTO.ProductoDetalleNestedDTO nestedProductoDetalleDTO = null;
        if (detalle.getProductoDetalle() != null) {
            ProductoDetalle pd = detalle.getProductoDetalle();
            nestedProductoDetalleDTO = OrdenCompraDetalleDTO.ProductoDetalleNestedDTO.builder()
                    .id(pd.getId())
                    .precioCompra(pd.getPrecioCompra())
                    .stockActual(pd.getStockActual())
                    .stockMaximo(pd.getStockMaximo())
                    .color(pd.getColor() != null ? pd.getColor().name() : null)
                    .talle(pd.getTalle() != null ? pd.getTalle().name() : null)
                    .productoDenominacion(pd.getProducto() != null ? pd.getProducto().getDenominacion() : null)
                    .build();
        }

        return OrdenCompraDetalleDTO.builder()
                .id(detalle.getId())
                .cantidad(detalle.getCantidad())
                .subtotal(detalle.getSubtotal())
                .ordenCompraId(detalle.getOrdenCompra() != null ? detalle.getOrdenCompra().getId() : null)
                .productoDetalleId(detalle.getProductoDetalle() != null ? detalle.getProductoDetalle().getId() : null)
                .productoDetalle(nestedProductoDetalleDTO)
                .build();
    }

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

    // --- Operaciones CRUD que devuelven DTOs ---

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> findAllDTO() throws Exception {
        List<OrdenCompra> entities = super.listar();
        return entities.stream()
                .map(this::mapOrdenCompraToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdenCompraDTO findByIdDTO(Long id) throws Exception {
        // Usar buscarPorId del propio servicio (que hereda de BaseService)
        OrdenCompra entity = super.buscarPorId(id);
        return mapOrdenCompraToDTO(entity);
    }

    @Transactional
    public OrdenCompra saveOrdenCompraFromDTO(OrdenCompraDTO dto) throws Exception {
        OrdenCompra ordenCompra = new OrdenCompra();

        ordenCompra.setDireccionEnvio(dto.getDireccionEnvio());
        ordenCompra.setFechaCompra(LocalDateTime.now());

        if (dto.getUsuario() != null && dto.getUsuario().getId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuario().getId()));
            ordenCompra.setUsuario(usuario);
        } else {
            throw new Exception("Los datos del usuario (al menos el ID) son obligatorios para crear una orden de compra.");
        }

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new Exception("La orden debe tener al menos un producto.");
        }

        Set<OrdenCompraDetalle> detalles = new HashSet<>();
        double totalOrden = 0.0;

        for (OrdenCompraDetalleDTO detalleDTO : dto.getDetalles()) {
            OrdenCompraDetalle detalle = new OrdenCompraDetalle();
            detalle.setCantidad(detalleDTO.getCantidad());

            Long productoDetalleId = detalleDTO.getProductoDetalle() != null ? detalleDTO.getProductoDetalle().getId() : null;
            if (productoDetalleId == null) {
                throw new Exception("El ID de ProductoDetalle es obligatorio para cada detalle de orden.");
            }

            ProductoDetalle productoDetalle = productoDetalleRepository.findById(productoDetalleId)
                    .orElseThrow(() -> new RuntimeException("Producto Detalle no encontrado con ID: " + productoDetalleId));

            detalle.setProductoDetalle(productoDetalle);
            detalle.setSubtotal(productoDetalle.getPrecioCompra() * detalle.getCantidad());
            detalle.setOrdenCompra(ordenCompra);

            detalles.add(detalle);
            totalOrden += detalle.getSubtotal();
        }

        ordenCompra.setDetalles(detalles);
        ordenCompra.setTotal(totalOrden);
        ordenCompra.setActivo(true);

        OrdenCompra savedOrden = super.crear(ordenCompra);
        return savedOrden;
    }

    @Transactional
    public OrdenCompra updateOrdenCompraFromDTO(Long id, OrdenCompraDTO dto) throws Exception {
        // Usar buscarPorId del propio servicio (que hereda de BaseService)
        OrdenCompra existingOrdenCompra = super.buscarPorId(id);

        existingOrdenCompra.setDireccionEnvio(dto.getDireccionEnvio());

        if (dto.getUsuario() != null && dto.getUsuario().getId() != null) {
            if (existingOrdenCompra.getUsuario() == null || !existingOrdenCompra.getUsuario().getId().equals(dto.getUsuario().getId())) {
                Usuario newUsuario = usuarioRepository.findById(dto.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuario().getId()));
                existingOrdenCompra.setUsuario(newUsuario);
            }
        }

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            existingOrdenCompra.getDetalles().clear();
        } else {
            existingOrdenCompra.getDetalles().clear();

            double totalOrden = 0.0;
            for (OrdenCompraDetalleDTO detalleDTO : dto.getDetalles()) {
                OrdenCompraDetalle detalle = new OrdenCompraDetalle();
                detalle.setCantidad(detalleDTO.getCantidad());

                Long productoDetalleId = detalleDTO.getProductoDetalle() != null ? detalleDTO.getProductoDetalle().getId() : null;
                if (productoDetalleId == null) {
                    throw new Exception("El ID de ProductoDetalle es obligatorio para cada detalle de orden.");
                }

                ProductoDetalle productoDetalle = productoDetalleRepository.findById(productoDetalleId)
                        .orElseThrow(() -> new RuntimeException("Producto Detalle no encontrado con ID: " + productoDetalleId));

                detalle.setProductoDetalle(productoDetalle);
                detalle.setSubtotal(productoDetalle.getPrecioCompra() * detalle.getCantidad());
                detalle.setOrdenCompra(existingOrdenCompra);

                existingOrdenCompra.addDetalle(detalle);
                totalOrden += detalle.getSubtotal();
            }
            existingOrdenCompra.setTotal(totalOrden);
        }

        OrdenCompra updatedOrden = super.actualizar(existingOrdenCompra);
        return updatedOrden;
    }

    // --- Métodos Adicionales ---

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerPorFecha(LocalDateTime fecha) throws Exception {
        try {
            List<OrdenCompra> entities = ordenCompraRepository.findAllByFechaCompra(fecha);
            return entities.stream()
                    .map(this::mapOrdenCompraToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al obtener ordenes por fecha: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerPorUsuarioDTO(Long userId) throws Exception {
        List<OrdenCompra> entities = ordenCompraRepository.findByUsuarioIdAndActivoTrue(userId);
        if (entities.isEmpty()) {
            throw new Exception("No se encontraron órdenes de compra activas para el usuario con ID: " + userId);
        }
        return entities.stream()
                .map(this::mapOrdenCompraToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminar(Long id) throws Exception {
        super.eliminar(id);
    }
}