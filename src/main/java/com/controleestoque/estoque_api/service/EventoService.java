package com.controleestoque.estoque_api.service;

import com.controleestoque.estoque_api.model.Equipamento;
import com.controleestoque.estoque_api.model.Evento;
import com.controleestoque.estoque_api.model.StatusEquipamento;
import com.controleestoque.estoque_api.repository.EquipamentoRepository;
import com.controleestoque.estoque_api.repository.EventoRepository;
import jdk.jshell.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    // 1. Criar o Evento (Com trava contra duplicidade)
    public Evento criarEvento(Evento evento) {

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
    public Evento adicionarEquipamentoNoEvento(Long eventoId, String idQrCode){
        //Passo 1 - Achar o evento pelo id
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(()-> new RuntimeException("Evento nao encontrado na base!"));

        //Passo 2 - Achar o equipamento pelo QRCode/Etiqueta
        Equipamento equipamento = equipamentoRepository.findById(idQrCode).orElseThrow(()-> new RuntimeException("Equipamento nao encontrado no galpao!"));
// TRAVA DE SEGURANÇA: Impede que peça estragada vá pro caminhão
        if (equipamento.getStatus() == StatusEquipamento.MANUTENCAO) {
            throw new RuntimeException("ERRO: Tá maluco? Este equipamento está na MANUTENÇÃO e não pode ir para o evento!");
        }

        // AUTOMAÇÃO: Muda o status para EM_USO e salva a peça atualizada
        equipamento.setStatus(StatusEquipamento.EM_USO);
        equipamentoRepository.save(equipamento);

        evento.getEquipamentos().add(equipamento);
        return eventoRepository.save(evento);
    }

    // 4. Descarregar o caminhao (Tirar o equipamento do evento)
    public Evento removerEquipamentoDoEvento(Long eventoId, String idQrCode, StatusEquipamento statusRetorno) {

        // Passo 1: Acha o evento pelo ID
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado na base!"));

        // Passo 2: Acha o equipamento pelo QRCode/Etiqueta
        Equipamento equipamento = equipamentoRepository.findById(idQrCode)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado no galpão!"));

        if(statusRetorno != null){
            equipamento.setStatus(statusRetorno);
        }else{
            equipamento.setStatus(StatusEquipamento.DISPONIVEL);
        }

        equipamentoRepository.save(equipamento);

        // Passo 3: Tira a peça da lista do evento
        evento.getEquipamentos().remove(equipamento);

        // Salva a atualização no banco de dados
        return eventoRepository.save(evento);
    }
}
