package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends BaseController<Usuario,Long>{
    public UsuarioController(UsuarioService usuarioService){
        super(usuarioService);
    }
    @Autowired
    private UsuarioService usuarioService;
}
