package com.controleestoque.estoque_api.controller;

import com.controleestoque.estoque_api.model.*;
import com.controleestoque.estoque_api.service.EquipamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipamentos") //Endereco principal.
@RequiredArgsConstructor
public class EquipamentoController {
    //linha direta com o Service
    private final EquipamentoService equipamentoService;
//
    //1- Recebe dados para cadastrar um cabo
    @PostMapping("/cabo")
    public Equipamento cadastrarCabo(@Valid @RequestBody Cabo cabo){
        return equipamentoService.cadastrar(cabo);
    }
//
    //2- Recebe dados para cadastrar materias da luz
    @PostMapping("/iluminacao")
    public Equipamento cadastrarIluminacao(@Valid @RequestBody Iluminacao iluminacao){
        return equipamentoService.cadastrar(iluminacao);
    }
//
    //3- Recebe dados para cadastrar materiais do audio
    @PostMapping("/audio")
    public Equipamento cadastrarAudio(@Valid @RequestBody Audio audio){
        return equipamentoService.cadastrar(audio);
    }

    //4- Recebe dados para cadastrar estruturas
    @PostMapping("/estrutura")
    public Equipamento cadastrarEstrutura(@Valid @RequestBody Estrutura estrutura){
        return equipamentoService.cadastrar(estrutura);
    }
//
    //5- Lista todos os equipamentos listados ate entao.
    @GetMapping
    public List<Equipamento> listarTodos(){
        return equipamentoService.listarTodos();
    }
//
    //6- Busca um equipamento especifico pelo QR Code digitado na URL
    @GetMapping("/{idQrCode}")
    public Equipamento buscarPorId(@PathVariable String idQrCode){
        return equipamentoService.buscarPorId(idQrCode);
    }
//
    //7- Deleta o equipamento pelo seu ID
    @DeleteMapping("{idQrCode}")
    public void deletar(@PathVariable String idQrCode){
        equipamentoService.deletar(idQrCode);
    }
//
    //8- Muda o status de um equipamento
    @PatchMapping("/{idQrCode}/status")
    public Equipamento atualizarStatus(@PathVariable String idQrCode,@RequestParam StatusEquipamento novoStatus){
        return equipamentoService.atualizarStatus(idQrCode, novoStatus);
    }

    //~~~~~~~~~~~~~~~~~~~ Usando um PUT para cada coisa ~~~~~~~~~~~~~~~~~~~//
    // Atualiza um Cabo inteiro
    @PutMapping("/cabo/{idQrCode}")
    public Equipamento atualizarCabo(@PathVariable String idQrCode, @Valid @RequestBody Cabo cabo) {
        cabo.setIdQrCode(idQrCode); // Garante que o objeto vai usar o ID que veio na URL
        return equipamentoService.atualizar(cabo);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    // Atualiza uma Iluminação inteira
    @PutMapping("/iluminacao/{idQrCode}")
    public Equipamento atualizarIluminacao(@PathVariable String idQrCode, @Valid @RequestBody Iluminacao iluminacao) {
        iluminacao.setIdQrCode(idQrCode); // Garante que o objeto vai usar o ID que veio na URL
        return equipamentoService.atualizar(iluminacao);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    // Atualiza um Áudio inteiro
    @PutMapping("/audio/{idQrCode}")
    public Equipamento atualizarAudio(@PathVariable String idQrCode,@Valid @RequestBody Audio audio) {
        audio.setIdQrCode(idQrCode); // Garante que o objeto vai usar o ID que veio na URL
        return equipamentoService.atualizar(audio);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    // Atualiza uma Estrutura inteira
    @PutMapping("/estrutura/{idQrCode}")
    public Equipamento atualizarEstrutura(@PathVariable String idQrCode, @Valid @RequestBody Estrutura estrutura) {
        estrutura.setIdQrCode(idQrCode); // Garante que o objeto vai usar o ID que veio na URL
        return equipamentoService.atualizar(estrutura);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    //Cadastrar PainelLed
    @PostMapping("/painel-led")
    public Equipamento cadastrarPainelLed(@Valid @RequestBody PainelLed painel){
        return equipamentoService.cadastrar(painel);
    }
    //Atualizar PainelLed
    @PutMapping("/painel-led/{idQrCode}")
    public Equipamento atualizarPainelLed(@PathVariable String idQrCode, @Valid @RequestBody PainelLed painel){
        // 1. Pega o ID da URL e "carimba" na nossa placa nova
        painel.setIdQrCode(idQrCode);
        // 2. Manda a placa completa (passando apenas 1 parâmetro)
        return equipamentoService.atualizar(painel);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    // GUICHÊ DE FILTRO 1: Busca apenas pelo Status
    // Exemplo de uso: GET http://localhost:8080/api/equipamentos/status/MANUTENCAO
    @GetMapping("/status/{status}")
    public List<Equipamento> buscarPorStatus(@PathVariable StatusEquipamento status){
        return equipamentoService.buscarPorStatus(status);
    }
    // GUICHÊ DE FILTRO 2: Busca por uma palavra no nome
    // Exemplo de uso: GET http://localhost:8080/api/equipamentos/nome/parled
    @GetMapping("/nome/{nome}")
    public List<Equipamento> buscarPorNome(@PathVariable String nome){
        return equipamentoService.buscarPorNome(nome);
    }
    // GUICHÊ DE FILTRO 3: Busca Combinada usando Parâmetros de URL (?chave=valor)
    // Exemplo de uso: GET http://localhost:8080/api/equipamentos/busca?status=DISPONIVEL&nome=cabo
    @GetMapping("/busca")
    public List<Equipamento> buscaAvancada(@RequestParam StatusEquipamento status,
                                           @RequestParam String nome){
        return equipamentoService.buscarPorStatusENome(status, nome);
    }











}
