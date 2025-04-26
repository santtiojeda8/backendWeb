package com.ecommerce.ecommerce.Services;


import com.ecommerce.ecommerce.Entities.Provincia;
import com.ecommerce.ecommerce.Repositories.ProvinciaRepository;

public class ProvinciaService extends BaseService<Provincia, Long> {
    public ProvinciaService(ProvinciaRepository provinciaRepository) {
        super(provinciaRepository);
    }

}
