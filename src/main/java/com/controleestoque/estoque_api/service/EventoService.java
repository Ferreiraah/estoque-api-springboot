package com.controleestoque.estoque_api.service;

import com.controleestoque.estoque_api.dto.EventoDTO;
import com.controleestoque.estoque_api.dto.ItemEventoDTO;
import com.controleestoque.estoque_api.enums.StatusEvento;
import com.controleestoque.estoque_api.exception.EquipamentoNaoEncontradoException;
import com.controleestoque.estoque_api.model.Equipamento;
import com.controleestoque.estoque_api.model.Evento;
import com.controleestoque.estoque_api.model.ItemEvento;
import com.controleestoque.estoque_api.enums.StatusEquipamento;
import com.controleestoque.estoque_api.repository.EquipamentoRepository;
import com.controleestoque.estoque_api.repository.EventoRepository;
import jakarta.transaction.Transactional;
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
    @Transactional
    public Evento criarEvento(EventoDTO eventoDTO) {

        // Monta o cabeçalho do evento
        Evento novoEvento = new Evento();
        novoEvento.setNome(eventoDTO.nome());
        novoEvento.setDataSaida(eventoDTO.dataSaida());
        novoEvento.setDataDevolucao(eventoDTO.dataDevolucao());
        novoEvento.setObservacoes(eventoDTO.observacoes());
        novoEvento.setStatus(StatusEvento.EM_PROGRESSO);

        // Processa item por item da lista que veio do Postman
        for (ItemEventoDTO itemDTO : eventoDTO.itens()) {

            // Vai no banco e busca o equipamento real pelo ID (seja LED, Audio, Cabo...)
            Equipamento equipamentoReal = equipamentoRepository.findById(itemDTO.idQrCode())
                    .orElseThrow(() -> new EquipamentoNaoEncontradoException(itemDTO.idQrCode()));

            // A TRANCA DE SEGURANÇA! (O OVERBOOKING)
            if (equipamentoReal.getStatus() != StatusEquipamento.DISPONIVEL) {
                throw new RuntimeException("Bloqueio de Romaneio: O equipamento " + equipamentoReal.getNome() +
                        " (" + equipamentoReal.getIdQrCode() + ") não está DISPONIVEL.");
            }

            // Altera o status para EM_USO
            equipamentoReal.setStatus(StatusEquipamento.EM_USO);

            // Monta o item do evento conectando o equipamento real
            ItemEvento novoItem = new ItemEvento();
            novoItem.setEvento(novoEvento);
            novoItem.setEquipamento(equipamentoReal);
            novoItem.setQuantidade(itemDTO.quantidade());

            // Adiciona na lista do evento
            novoEvento.getItens().add(novoItem);
        }

        // Salva tudo (Evento, Itens e atualiza os Equipamentos) de uma vez só!
        return eventoRepository.save(novoEvento);
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

        if (equipamento.getStatus() == StatusEquipamento.EM_USO) {
            throw new RuntimeException("Operação negada! Este equipamento já está rodando em outro evento.");
        }
        if (equipamento.getStatus() == StatusEquipamento.MANUTENCAO) {
            throw new RuntimeException("Segura aí! Este equipamento está na bancada de manutenção.");
        }

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

            if (equipamento.getStatus() == StatusEquipamento.EM_USO) {
                throw new RuntimeException("Operação negada! O equipamento " + idQrCode + " já está rodando em outro evento.");
            }
            if (equipamento.getStatus() == StatusEquipamento.MANUTENCAO) {
                throw new RuntimeException("Segura aí! O equipamento " + idQrCode + " está na bancada de manutenção.");
            }

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

    @Transactional
    public Evento devolverEquipamentosEmLote(Long eventoId, List<String> idsQrCodes) {
        // Faz um loop por todos os QR Codes que foram bipados na devolução
        for(String qrCode : idsQrCodes) {
            removerEquipamentoDoEvento(eventoId, qrCode, StatusEquipamento.DISPONIVEL);
        }
        return buscarPorId(eventoId);
    }

    @Transactional
    public Evento finalizarEvento(Long eventoId){
        // Busca o evento no banco
        Evento evento = buscarPorId(eventoId);
        // Regra de negócio: O caminhão voltou vazio?
        // Verifica se a lista de itens vinculados a esse evento está vazia.
        if(!evento.getItens().isEmpty()){
            throw new RuntimeException("Cuidado!, não da para fechar a OS. Ainda tem equipamento que nao foi devolvido");
        }

        // Se passou pela barreira acima, muda o status da OS
        evento.setStatus(StatusEvento.FINALIZADO);

        // Salva a alteracao no banco e devolve o evento atualizado
        return eventoRepository.save(evento);
    }

    public Evento criarRomaneio(Evento evento) {

        // 1. Antes de salvar qualquer coisa, conferimos item por item na porta
        for (ItemEvento item : evento.getItens()) {

            // Busca o equipamento atualizado direto do banco de dados para evitar fraudes
            Equipamento equipamentoReal = equipamentoRepository.findById(item.getEquipamento().getIdQrCode())
                    .orElseThrow(() -> new RuntimeException("Equipamento não encontrado!"));

            // 2. A barreira de segurança!
            if (equipamentoReal.getStatus() != StatusEquipamento.DISPONIVEL) {
                throw new RuntimeException(
                        "Bloqueio de Romaneio: O equipamento " + equipamentoReal.getNome() +
                                " (Código: " + equipamentoReal.getIdQrCode() + ") não está disponível. Status atual: " + equipamentoReal.getStatus()
                );
            }

            // Se passar pela barreira, já mudamos o status dele para a saída
            equipamentoReal.setStatus(StatusEquipamento.EM_USO);

            // Garante que o item do evento está apontando para o equipamento atualizado
            item.setEquipamento(equipamentoReal);
        }

        // 3. Se nenhum equipamento estourou erro, salva o evento e os itens no banco
        return eventoRepository.save(evento);
    }



}
