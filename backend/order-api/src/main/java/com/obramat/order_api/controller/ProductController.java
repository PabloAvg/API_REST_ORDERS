package com.obramat.order_api.controller;

import com.obramat.order_api.dto.ProductResponse;
import com.obramat.order_api.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ProductResponse> search(@RequestParam(required = false) String name) {
        var products = (name == null || name.isBlank())
                ? repo.findAll()
                // Containing → hace búsqueda parcial (substring). Es decir, si buscas "ce", coincide "Cemento".
                // IgnoreCase → ignora mayúsculas/minúsculas, así "CE" y "ce" dan lo mismo.
                // Esto cumple literalmente “búsqueda parcial e insensible a mayúsculas”
                : repo.findByNameContainingIgnoreCase(name);

        // Convertir entidad a DTO
        return products.stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice()))
                .toList();
    }
}
