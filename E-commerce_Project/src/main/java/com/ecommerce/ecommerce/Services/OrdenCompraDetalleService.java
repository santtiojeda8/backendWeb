package com.ecommerce.ecommerce.Services;


import com.ecommerce.ecommerce.Entities.OrdenCompraDetalle;
import com.ecommerce.ecommerce.Repositories.OrdenCompraDetalleRepository;
import com.ecommerce.ecommerce.Services.BaseService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenCompraDetalleService extends BaseService<OrdenCompraDetalle, Long> {

    private final OrdenCompraDetalleRepository detalleRepository;

    public OrdenCompraDetalleService(OrdenCompraDetalleRepository detalleRepository) {
        super(detalleRepository);
        this.detalleRepository = detalleRepository;
    }
    public List<OrdenCompraDetalle> obtenerPorOrdenCompra(Long ordenCompraId) throws Exception{
        try{
            return detalleRepository.findAllByOrdenCompraId(ordenCompraId);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public List<OrdenCompraDetalle> obtenerPorProductoDetalle(Long productoDetalleId) throws Exception{
        try{
            return detalleRepository.findAllByProductoDetalleId(productoDetalleId);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    @Override
    public OrdenCompraDetalle crear(OrdenCompraDetalle detalle) throws Exception {
        try{
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new Exception("La cantidad debe ser mayor a 0.");
            }

            if (detalle.getProductoDetalle() == null) {
                throw new Exception("Debe asociar un producto al detalle.");
            }

            return super.crear(detalle);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }
}
