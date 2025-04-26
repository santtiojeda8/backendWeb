package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Cliente;
import com.ecommerce.ecommerce.Services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController extends BaseController<Cliente, Long> {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        super(clienteService);
        this.clienteService = clienteService;
    }

    // Buscar cliente por ID de usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Cliente> findByUsuarioId(@PathVariable Long idUsuario) {
        try {
            Cliente cliente = clienteService.findByUsuarioId(idUsuario);
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // Buscar cliente por ID de imagen asociada
    @GetMapping("/imagen/{idImagen}")
    public ResponseEntity<Cliente> findByImagenPersonaId(@PathVariable Long idImagen) {
        try {
            Cliente cliente = clienteService.findByImagenPersonaId(idImagen);
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
