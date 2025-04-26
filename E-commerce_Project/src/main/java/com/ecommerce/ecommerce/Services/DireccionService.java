package com.ecommerce.ecommerce.Services;



import com.ecommerce.ecommerce.Entities.Direccion;
import com.ecommerce.ecommerce.Repositories.DireccionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DireccionService extends BaseService<Direccion, Long> {
    @Autowired
    private DireccionRepository direccionRepository;
    public DireccionService(DireccionRepository direccionRepository){
        super(direccionRepository);
    }
    @Transactional
    public List<Direccion> listarPorLocalidad(Long idLocalidad)throws Exception{
        try{
            return direccionRepository.findAllByLocalidadId(idLocalidad);
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    @Transactional
    public List<Direccion> listarPorClientesID(Long idCliente)throws Exception{
        try{
            return direccionRepository.findAllByClientesAndId(idCliente);
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

}