package com.controleestoque.estoque_api.dto;

import java.time.LocalDate;
import java.util.List;

public record EventoDTO(
        String nome,
        LocalDate dataSaida,
        LocalDate dataDevolucao,
        String observacoes,
        List<ItemEventoDTO> itens
) {}

