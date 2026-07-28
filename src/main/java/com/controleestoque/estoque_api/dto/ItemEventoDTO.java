package com.controleestoque.estoque_api.dto;

// Este record fica no mesmo arquivo, ou você pode criar outro separado.
public record ItemEventoDTO(
        String idQrCode, // Recebemos apenas o ID
        Integer quantidade
) {}
