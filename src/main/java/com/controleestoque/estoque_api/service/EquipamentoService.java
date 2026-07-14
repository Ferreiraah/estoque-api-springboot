package com.controleestoque.estoque_api.service;


import com.controleestoque.estoque_api.exception.EquipamentoNaoEncontradoException;
import com.controleestoque.estoque_api.model.Equipamento;
import com.controleestoque.estoque_api.model.StatusEquipamento;
import com.controleestoque.estoque_api.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipamentoService {
    //Service tem a linha direta com o Repository
    private final EquipamentoRepository equipamentoRepository;

    //1 - Cadastrar um equipamento novo no galpao
    public Equipamento cadastrar(Equipamento equipamento) {
        //Mais pra frente vai entrar a regra:Esse QR ja existe?
        return equipamentoRepository.save(equipamento);
    }

    //2 - Listar tudo que existe no galpao
    public List<Equipamento> listarTodos(){
        return equipamentoRepository.findAll();
    }

    //3- Buscar um equipamento pelo QRCode
    public Equipamento buscarPorId(String idQrCode){
        return equipamentoRepository.findById(idQrCode)
                .orElseThrow(()-> new EquipamentoNaoEncontradoException(idQrCode));
    }
    //4- Deletar um equipamento do galpao
    public void deletar(String idQrCode){
        Equipamento equipamento = buscarPorId(idQrCode);
        equipamentoRepository.delete(equipamento);
    }

    //5- Atualizar apenas o status de um equipamento
    public Equipamento atualizarStatus(String idQrCode, StatusEquipamento novoStatus){

        Equipamento equipamento = buscarPorId(idQrCode);

        equipamento.setStatus(novoStatus);

        return equipamentoRepository.save(equipamento);
    }
    //6- Atualizacao completa de um equipamento (PUT)
    public Equipamento atualizar(Equipamento equipamento){
        // Verifica se o QRCode realmente existe antes de atualizar.
        if(!equipamentoRepository.existsById(equipamento.getIdQrCode())){
            throw new RuntimeException(
                    "Impossivel atualizar. Equipamento nao encontrado" + equipamento.getIdQrCode());
        }
        // Se existe, o save() faz a atualizacao automatica de todas as tabelas envolvidas.
        return equipamentoRepository.save(equipamento);
    }

    //7 - Filtro por Status
    public List<Equipamento> buscarPorStatus(StatusEquipamento status){
        return equipamentoRepository.findByStatus(status);
    }

    //8 - Filtro por Palavra-chave no nome
    public List<Equipamento> buscarPorNome(String nome){
        return equipamentoRepository.findByNomeContainingIgnoreCase(nome);
    }

    //9 - Filtro Combinado (Status + nome)
    public List<Equipamento> buscarPorStatusENome (StatusEquipamento status, String nome){
        return equipamentoRepository.findByStatusAndNomeContainingIgnoreCase(status, nome);
    }







}
