package com.controleestoque.estoque_api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice //Avisa para o Spring que ele é o segurança que cuida dos erros de toda a API
public class GlobalExceptionHandler {

    @ExceptionHandler(EquipamentoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleEquipamentoNaoEncontrado(EquipamentoNaoEncontradoException ex){

        //Criando um mapa para montar um JSON 'bonito' com os detalhes do erro
        Map<String, Object> corpoErro = new HashMap<>();
        corpoErro.put("timestamp", LocalDateTime.now()); //Hora exata do acontecimento
        corpoErro.put("status", HttpStatus.NOT_FOUND.value()); //Numero 404
        corpoErro.put("erro", "Recurso nao encontrado");
        corpoErro.put("mensagem", ex.getMessage()); //Pega o texto que foi feito na Exception

        return new ResponseEntity<>(corpoErro, HttpStatus.NOT_FOUND);

    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    // Avisa que quando a validação falhar, este metodo entra em ação
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(org.springframework.web.bind.MethodArgumentNotValidException ex) {

        Map<String, Object> corpoErro = new HashMap<>();
        corpoErro.put("timestamp", LocalDateTime.now());
        corpoErro.put("status", HttpStatus.BAD_REQUEST.value()); // Número 400
        corpoErro.put("erro", "Erro de Validação de Dados");

        // Como podem ter vários erros de uma vez (ex: sem nome E com tamanho negativo),
        // a gente cria uma lista para pegar as mensagens que foram escritas no model
        List<String> mensagens = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                mensagens.add(error.getDefaultMessage())
        );

        corpoErro.put("mensagens", mensagens);

        return new ResponseEntity<>(corpoErro, HttpStatus.BAD_REQUEST);
    }

    // Avisa que quando uma regra de negócio for quebrada (ex: Trava de Segurança), este metodo entra em ação
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRegrasDeNegocio(RuntimeException ex){

        Map<String, Object> corpoErro = new HashMap<>();
        corpoErro.put("timestamp", LocalDateTime.now());
        corpoErro.put("status", HttpStatus.BAD_REQUEST.value()); // Número 400
        corpoErro.put("erro", "Violação de Regra de Negócio");
        corpoErro.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(corpoErro, HttpStatus.BAD_REQUEST);
    }

}
