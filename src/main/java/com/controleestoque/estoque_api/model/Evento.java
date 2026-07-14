package com.controleestoque.estoque_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Evento{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do evento nao pode ficar em branco!")
    private String nome;

    @NotNull(message = "A data de saida e obrigatoria!")
    private LocalDate dataSaida;

    @NotNull(message = "A data de devolucao e obrigatoria!")
    private LocalDate dataDevolucao;

    // O Spring cria uma tabela invisível só para gerenciar qual equipamento foi pra qual evento.
    @ManyToMany
    @JoinTable(
            name = "evento_equipamento", // Nome da tabela que vai unir os dois mundos
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "equipamento_id")
    )
    private List<Equipamento> equipamentos = new ArrayList<>();

}
