package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Admin;
import com.ecommerce.ecommerce.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")

public class AdminController extends BaseController<Admin, Long> {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        super(adminService);
        this.adminService = adminService;
    }

    // Obtener admin por nombre de usuario
    @GetMapping("/username/{userName}")
    public ResponseEntity<Admin> getByUserName(@PathVariable String userName) {
        try {
            Admin admin = adminService.findByUserName(userName);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Obtener admin por imagen de usuario
    @GetMapping("/imagen/{idImagen}")
    public ResponseEntity<Admin> getByImagenUserId(@PathVariable Long idImagen) {
        try {
            Admin admin = adminService.findByImagenUserId(idImagen);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}

