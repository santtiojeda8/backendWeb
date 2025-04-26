package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Localidad;
import com.ecommerce.ecommerce.Services.LocalidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/localidades")
public class LocalidadController extends BaseController<Localidad, Long> {

    private final LocalidadService localidadService;

    @Autowired
    public LocalidadController(LocalidadService localidadService) {
        super(localidadService);
        this.localidadService = localidadService;
    }

    @GetMapping("/provincia/{idProvincia}")
    public List<Localidad> listarPorProvincia(@PathVariable Long idProvincia) throws Exception {
        return localidadService.listarPorProvincia(idProvincia);
    }
}
