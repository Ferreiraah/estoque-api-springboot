package com.controleestoque.estoque_api.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Equipamento {

    @Id
    @NotBlank(message = "O QRCode nao pode estar em branco!")
    private String idQrCode;

    @NotBlank(message = "Todo equipamento precisa ter um nome!")
    private String nome;

    @NotNull(message = "O status do equipamento e obrigatorio!")
    @Enumerated(EnumType.STRING)
    private StatusEquipamento status;

    // Link da foto do equipamento para aparecer na lojinha
    private String imagemUrl;
}
