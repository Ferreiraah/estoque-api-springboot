package com.controleestoque.estoque_api.controller;

import com.controleestoque.estoque_api.model.Evento;
import com.controleestoque.estoque_api.model.StatusEquipamento;
import com.controleestoque.estoque_api.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // 1. ABRIR NOVA OS (CRIAR EVENTO)
    // Exemplo: POST http://localhost:8080/api/eventos
    @PostMapping
    public Evento criarEvento(@Valid @RequestBody Evento evento){
        return eventoService.criarEvento(evento);
    }

    // 2. Ver a agenda (LISTAR TODOS OS EVENTOS)
    // Exemplo: GET http://localhost:8080/api/eventos
    @GetMapping
    public List<Evento> listarEventos(){
        return  eventoService.listarEventos();
    }

    // 3. Carregar o caminhao (ADICIONAR EQUIPAMENTO NO EVENTO)
    // Exemplo: POST http://localhost:8080/api/eventos/1/equipamentos/LED-P39-001
    @PostMapping("/{eventoId}/equipamentos/{idQrCode}")
    public Evento adicionarEquipamento(@PathVariable Long eventoId,
                                       @PathVariable String idQrCode){
        return eventoService.adicionarEquipamentoNoEvento(eventoId, idQrCode);
    }

    // 4. Tirar do Caminhao (REMOVER EQUIPAMENTO DO EVENTO)
    // Exemplo: DELETE http://localhost:8080/api/eventos/2/equipamentos/LED-P39-001
    // Exemplo Quebrado: DELETE http://localhost:8080/api/eventos/2/equipamentos/LED-P39-001?statusRetorno=MANUTENCAO
    @DeleteMapping("/{eventoId}/equipamentos/{idQrCode}")
    public Evento removerEquipamento(
            @PathVariable Long eventoId,
            @PathVariable String idQrCode,
            @RequestParam(required = false) StatusEquipamento statusRetorno){

        return eventoService.removerEquipamentoDoEvento(eventoId, idQrCode, statusRetorno);
    }
}
