// src/main/java/com/ecommerce/ecommerce/Services/ProvinciaService.java
package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Provincia;
import com.ecommerce.ecommerce.Repositories.ProvinciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinciaService extends BaseService<Provincia, Long> {

    private final ProvinciaRepository provinciaRepository;

    public ProvinciaService(ProvinciaRepository provinciaRepository) {
        super(provinciaRepository); // Llama al constructor del BaseService
        this.provinciaRepository = provinciaRepository;
    }

}