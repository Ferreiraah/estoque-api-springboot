package com.controleestoque.estoque_api.repository;


import com.controleestoque.estoque_api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
