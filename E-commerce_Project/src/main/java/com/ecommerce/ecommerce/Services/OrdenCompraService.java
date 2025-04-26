package com.ecommerce.ecommerce.Services;



import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Repositories.OrdenCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenCompraService extends BaseService<OrdenCompra, Long> {

    private final OrdenCompraRepository ordenCompraRepository;

    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository) {
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;
    }

    @Override
    public OrdenCompra crear(OrdenCompra ordenCompra) throws Exception {
        try{
            if (ordenCompra.getFechaCompra() == null) {
                ordenCompra.setFechaCompra(LocalDateTime.now());
            }

            if (ordenCompra.getDetalles() == null || ordenCompra.getDetalles().isEmpty()) {
                throw new Exception("La orden debe tener al menos un producto.");
            }

            double totalCalculado = ordenCompra.getDetalles().stream()
                    .mapToDouble(det -> det.getProductoDetalle().getPrecioCompra() * det.getCantidad())
                    .sum();

            if (ordenCompra.getDescuento() != null) {
                totalCalculado -= ordenCompra.getDescuento().getPrecioPromocional();
            }

            ordenCompra.setTotal(totalCalculado);

            // Asignar referencia de orden a cada detalle (importante si usás persistencia en cascada)
            for (OrdenCompraDetalle detalle : ordenCompra.getDetalles()) {
                detalle.setOrdenCompra(ordenCompra);
            }

            return super.crear(ordenCompra);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public List<OrdenCompra> obtenerPorFecha(LocalDateTime fecha) throws Exception {
        try{
            return ordenCompraRepository.findAllByFechaCompra(fecha);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public List<OrdenCompra> obtenerPorDescuento(Long idDescuento) throws Exception {
        try{
            return ordenCompraRepository.findAllByDescuentoId(idDescuento);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }
}
