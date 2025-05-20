package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Provincia;
import com.ecommerce.ecommerce.Services.ProvinciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/provincia")

public class ProvinciaController extends BaseController<Provincia, Long> {

    private final ProvinciaService provinciaService;

    @Autowired
    public ProvinciaController(ProvinciaService provinciaService) {
        super(provinciaService);
        this.provinciaService = provinciaService;
    }
}
