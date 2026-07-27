package com.controleestoque.estoque_api.service;

import com.controleestoque.estoque_api.enums.StatusEvento;
import com.controleestoque.estoque_api.model.Equipamento;
import com.controleestoque.estoque_api.model.Evento;
import com.controleestoque.estoque_api.model.ItemEvento;
import com.controleestoque.estoque_api.enums.StatusEquipamento;
import com.controleestoque.estoque_api.repository.EquipamentoRepository;
import com.controleestoque.estoque_api.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    // 1. Criar o Evento (Com trava contra duplicidade)
    public Evento criarEvento(Evento evento) {

        evento.setStatus(StatusEvento.EM_PROGRESSO);

        // Verifica se a agenda já tem um show igual nas mesmas datas
        boolean eventoJaExiste = eventoRepository.existsByNomeAndDataSaidaAndDataDevolucao(
                evento.getNome(),
                evento.getDataSaida(),
                evento.getDataDevolucao()
        );

        if (eventoJaExiste) {
            throw new RuntimeException("Calma lá! Já existe uma OS aberta para esse mesmo evento nestas datas.");
        }

        return eventoRepository.save(evento);
    }

    //2 - Listar todos os Eventos
    public List<Evento> listarEventos(){
        return eventoRepository.findAll();
    }

    //3 - Colocar equipamento no caminhao
    public Evento adicionarEquipamentoNoEvento(Long eventoId, String idQrCode, Integer quantidade) {
        // 1. Acha o evento
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado!"));

        // 2. Acha o equipamento pelo QRCode
        Equipamento equipamento = equipamentoRepository.findById(idQrCode)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado no galpão!"));

        // 3. Verifica se o equipamento já está na lista de itens do evento
        Optional<ItemEvento> itemExistente = evento.getItens().stream()
                .filter(item -> item.getEquipamento().getIdQrCode().equals(idQrCode))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Se já tem no carrinho, só aumenta a quantidade
            ItemEvento item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            // Se é o primeiro a ser bipado, cria o vínculo
            ItemEvento novoItem = new ItemEvento();
            novoItem.setEvento(evento);
            novoItem.setEquipamento(equipamento);
            novoItem.setQuantidade(quantidade);

            evento.getItens().add(novoItem);
        }

        // Opcional: Atualiza o status do equipamento para "EM_USO" ou "SEPARADO"
        equipamento.setStatus(StatusEquipamento.EM_USO);
        equipamentoRepository.save(equipamento);

        // Salva o evento (o CascadeType.ALL vai salvar os ItensEvento automaticamente)
        return eventoRepository.save(evento);
    }

    // 4. Descarregar o caminhao (Tirar o equipamento do evento)
    public Evento removerEquipamentoDoEvento(Long eventoId, String idQrCode, StatusEquipamento statusRetorno) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado!"));

        Equipamento equipamento = equipamentoRepository.findById(idQrCode)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado no galpão!"));

        // Acha o item específico no carrinho
        ItemEvento itemParaRemover = evento.getItens().stream()
                .filter(item -> item.getEquipamento().getIdQrCode().equals(idQrCode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Esse equipamento não está neste evento!"));

        // Se tem mais de 1, diminui a quantidade. Se for o último, remove o item inteiro.
        if (itemParaRemover.getQuantidade() > 1) {
            itemParaRemover.setQuantidade(itemParaRemover.getQuantidade() - 1);
        } else {
            evento.getItens().remove(itemParaRemover);
        }

        // A sua lógica original de manter o status de retorno
        if (statusRetorno != null) {
            equipamento.setStatus(statusRetorno);
        } else {
            equipamento.setStatus(StatusEquipamento.DISPONIVEL);
        }

        equipamentoRepository.save(equipamento);

        return eventoRepository.save(evento);
    }

    public Evento buscarPorId(Long id){
        return eventoRepository.findById(id).orElseThrow(()-> new RuntimeException("Romaneio nao encontrado para o ID" + id));
    }

    //================================================================================================================//

    // 5. Alocar equipamentos em lote (Romaneio por lista de QR Codes)
    public Evento alocarEquipamentosEmLote(Long eventoId, List<String> idsQrCodes) {
        // 1. Acha o evento
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado!"));

        // 2. Itera sobre a lista de QR Codes recebida
        for (String idQrCode : idsQrCodes) {
            Equipamento equipamento = equipamentoRepository.findById(idQrCode)
                    .orElseThrow(() -> new RuntimeException("Equipamento não encontrado no galpão: " + idQrCode));

            // 3. Verifica se o equipamento já está na lista de itens do evento
            Optional<ItemEvento> itemExistente = evento.getItens().stream()
                    .filter(item -> item.getEquipamento().getIdQrCode().equals(idQrCode))
                    .findFirst();

            if (itemExistente.isPresent()) {
                ItemEvento item = itemExistente.get();
                item.setQuantidade(item.getQuantidade() + 1);
            } else {
                ItemEvento novoItem = new ItemEvento();
                novoItem.setEvento(evento);
                novoItem.setEquipamento(equipamento);
                novoItem.setQuantidade(1); // Padrão 1 por item na lista em lote

                evento.getItens().add(novoItem);
            }

            // Atualiza o status do equipamento para em uso
            equipamento.setStatus(StatusEquipamento.EM_USO);
            equipamentoRepository.save(equipamento);
        }

        // Salva o evento com todos os itens novos de uma vez só
        return eventoRepository.save(evento);
    }

}
