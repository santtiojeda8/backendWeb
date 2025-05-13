package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Categoria;
import com.ecommerce.ecommerce.Entities.Descuento;
import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Repositories.ProductoRepository;
import com.ecommerce.ecommerce.dto.ProductoDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService extends BaseService<Producto, Long> {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        super(productoRepository);
        this.productoRepository = productoRepository;
    }

    @Transactional
    public List<Producto> findProductosConPromocion() throws Exception  {
        try{
            return productoRepository.findAll().stream()
                    .filter(Producto::isTienePromocion)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public List<Producto> buscarPorNombre(String keyword)  throws Exception {
        try{
            return productoRepository.findByDenominacionContainingIgnoreCase(keyword);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    // Método para mapear una entidad Producto a un ProductoDTO
    public ProductoDTO mapearProductoADTO(Producto producto) {
        Double precioFinal = calcularPrecioFinal(producto);

        List<String> imagenesUrls = producto.getImagenes().stream()
                .map(imagen -> imagen.getDenominacion()) // Asume que getDenominacion() devuelve la URL
                .collect(Collectors.toList());
        List<String> categorias = producto.getCategorias().stream()
                .map(Categoria::getDenominacion) // Asume que Categoria tiene getDenominacion()
                .collect(Collectors.toList());

        return new ProductoDTO(
                producto.getId(),
                producto.getDenominacion(),
                producto.getPrecioVenta(), // Precio original
                precioFinal, // Precio con descuento aplicado si aplica
                categorias,
                producto.getSexo(),
                producto.isTienePromocion(),
                imagenesUrls
        );
    }

    // Método para calcular el precio final aplicando descuentos activos
    public Double calcularPrecioFinal(Producto producto) {
        Double precioActual = producto.getPrecioVenta(); // Empezamos con el precio de venta

        // Si no tiene promoción, no aplicamos descuentos
        if (!producto.isTienePromocion() || producto.getDescuentos().isEmpty()) {
            return precioActual;
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // Iteramos sobre los descuentos asociados al producto
        for (Descuento descuento : producto.getDescuentos()) {
            // Verificamos si el descuento está activo en la fecha y hora actual
            if ((descuento.getFechaDesde().isBefore(hoy) || descuento.getFechaDesde().isEqual(hoy)) &&
                    (descuento.getFechaHasta().isAfter(hoy) || descuento.getFechaHasta().isEqual(hoy)) &&
                    (descuento.getHoraDesde().isBefore(ahora) || descuento.getHoraDesde().equals(ahora)) &&
                    (descuento.getHoraHasta().isAfter(ahora) || descuento.getHoraHasta().equals(ahora))) {

                // Aplicamos el descuento. Asumimos que getPrecioPromocional() es un porcentaje (ej. 0.10 para 10%)
                precioActual = precioActual * (1 - descuento.getPrecioPromocional());

                // Si solo quieres aplicar el primer descuento válido que encuentres, puedes romper el bucle aquí
                // break;
            }
        }

        return precioActual; // Retorna el precio después de aplicar descuentos válidos
    }

    // --- MÉTODO CORREGIDO ---
    // Este método obtiene TODOS los productos y los mapea a DTOs.
    // El mapeo incluye el cálculo del precio final, que ya considera si hay promoción o no.
    @Transactional // Asegura que la operación sea atómica
    public List<ProductoDTO> obtenerTodosLosProductosDTO() throws Exception {
        try {
            // Obtener todos los productos sin filtrar
            List<Producto> todosLosProductos = productoRepository.findAll();

            // Mapear cada Producto a ProductoDTO
            List<ProductoDTO> productosDTO = todosLosProductos.stream()
                    .map(this::mapearProductoADTO) // Usamos el método de mapeo que calcula el precio final
                    .collect(Collectors.toList());

            return productosDTO;
        } catch (Exception e) {
            throw new Exception("Error al obtener todos los productos DTO: " + e.getMessage());
        }
    }

    // --- MÉTODO PARA OBTENER SOLO PROMOCIONALES EN DTO ---
    // Renombramos el método original para que sea más claro su propósito
    @Transactional // Asegura que la operación sea atómica
    public List<ProductoDTO> obtenerProductosPromocionalesDTO() throws Exception {
        try {
            // Obtener todos los productos y filtrar solo los promocionales
            List<Producto> productosPromocionales = productoRepository.findAll().stream()
                    .filter(Producto::isTienePromocion) // Filtramos por promoción
                    .collect(Collectors.toList());

            // Mapear cada Producto promocional a ProductoDTO
            List<ProductoDTO> productosPromocionalesDTO = productosPromocionales.stream()
                    .map(this::mapearProductoADTO) // Usamos el método de mapeo que calcula el precio final
                    .collect(Collectors.toList());

            return productosPromocionalesDTO;
        } catch (Exception e) {
            throw new Exception("Error al obtener productos promocionales DTO: " + e.getMessage());
        }
    }

    // Método para obtener un ProductoDTO por su ID
    @Transactional // Asegura que la operación sea atómica
    public ProductoDTO obtenerProductoDTOPorId(Long id) throws Exception {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + id));

            return mapearProductoADTO(producto);
        } catch (Exception e) {
            throw new Exception("Error al obtener Producto DTO por ID: " + e.getMessage());
        }
    }
}
