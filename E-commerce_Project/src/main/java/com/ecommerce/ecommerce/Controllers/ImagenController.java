package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Imagen;
import com.ecommerce.ecommerce.Services.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/imagen")
public class ImagenController extends BaseController<Imagen, Long> {

    private final ImagenService imagenService;

    @Autowired
    public ImagenController(ImagenService imagenService) {
        super(imagenService);
        this.imagenService = imagenService;
    }
}

