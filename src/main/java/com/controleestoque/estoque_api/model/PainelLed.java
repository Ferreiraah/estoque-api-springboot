package com.controleestoque.estoque_api.model;

import com.controleestoque.estoque_api.controller.EquipamentoController;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PainelLed extends Equipamento {

    @NotBlank(message = "O modelo não pode ficar em branco! Ex: P3.9, P4")
    private String modelo;

    @NotBlank(message = "A medida da placa é obrigatória! Ex: 0.50x1.00")
    private String medida;

    // Se for true, aguenta chuva. Se for false, é só uso interno.
    private boolean outdoor;

}
