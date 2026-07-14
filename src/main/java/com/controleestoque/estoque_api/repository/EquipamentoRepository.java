package com.controleestoque.estoque_api.repository;

import com.controleestoque.estoque_api.model.Equipamento;
import com.controleestoque.estoque_api.model.StatusEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, String> {

    //1 - Filtro por Status
    List<Equipamento> findByStatus(StatusEquipamento status);

    // 2 - FILTRO POR NOME
    // O 'Containing' faz a busca parcial (tipo o LIKE %nome% do SQL)
    // O 'IgnoreCase' ignora letras maiúsculas e minúsculas
    List<Equipamento> findByNomeContainingIgnoreCase(String nome);

    //3 - Filtro Combinado
    //O 'And' justa duas condicoes obrigatorias no mesmo SQL
    List<Equipamento> findByStatusAndNomeContainingIgnoreCase(StatusEquipamento status, String nome);
}
