package com.controleestoque.estoque_api.model;

import com.controleestoque.estoque_api.enums.StatusEvento;
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

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEvento> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private StatusEvento status = StatusEvento.EM_PROGRESSO;

    @Column(columnDefinition = "TEXT")
    private String observacoes;



}
