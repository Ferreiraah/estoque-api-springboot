package com.controleestoque.estoque_api.repository;

import com.controleestoque.estoque_api.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, String> {}
