package com.controleestoque.estoque_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "itens_evento")
@Data
@NoArgsConstructor
public class ItemEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    @JsonIgnore
    private Evento evento;

    @ManyToOne
    @JoinColumn(name= "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Column(nullable = false)
    private Integer quantidade;





}
