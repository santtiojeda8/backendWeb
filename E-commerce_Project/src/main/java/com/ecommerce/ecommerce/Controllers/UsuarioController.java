package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController extends BaseController<Usuario, Long> {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        super(usuarioService);
        this.usuarioService = usuarioService;
    }
}
