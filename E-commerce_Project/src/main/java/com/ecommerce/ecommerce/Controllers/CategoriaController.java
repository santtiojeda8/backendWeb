package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Categoria;
import com.ecommerce.ecommerce.Services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController extends BaseController<Categoria, Long> {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        super(categoriaService);
        this.categoriaService = categoriaService;
    }

    // Agregar subcategoría a una categoría padre
    @PostMapping("/{idCategoriaPadre}/subcategoria")
    public ResponseEntity<Categoria> agregarSubcategoria(@PathVariable Long idCategoriaPadre, @RequestBody Categoria nuevaSubcategoria) {
        try {
            Categoria subcategoriaCreada = categoriaService.agregarSubcategoria(idCategoriaPadre, nuevaSubcategoria);
            if (subcategoriaCreada != null) {
                return ResponseEntity.status(201).body(subcategoriaCreada);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Listar subcategorías de una categoría padre
    @GetMapping("/{idCategoriaPadre}/subcategorias")
    public ResponseEntity<List<Categoria>> listarSubcategorias(@PathVariable Long idCategoriaPadre) {
        try {
            List<Categoria> subcategorias = categoriaService.listarPorCategoriaPadre(idCategoriaPadre);
            return ResponseEntity.ok(subcategorias);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}
