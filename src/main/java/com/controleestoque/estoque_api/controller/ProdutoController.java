package com.controleestoque.estoque_api.controller;


import com.controleestoque.estoque_api.model.Produto;
import com.controleestoque.estoque_api.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @PostMapping
    public ResponseEntity<Produto> cadastrar(@RequestBody Produto produto){
        Produto novoProduto = repository.save(produto);
        return ResponseEntity.ok(novoProduto);
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listar(){
        return ResponseEntity.ok(repository.findAll());
    }
}
