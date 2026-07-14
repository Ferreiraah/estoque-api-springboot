package com.controleestoque.estoque_api.repository;

import com.controleestoque.estoque_api.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    boolean existsByNomeAndDataSaidaAndDataDevolucao(String nome, LocalDate dataSaida, LocalDate dataDevolucao);


}
