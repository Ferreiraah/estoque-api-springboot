package com.controleestoque.estoque_api.model;


import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cabo extends Equipamento {

    @NotBlank (message = "O tipo do cabo nao pode ficar em branco!")
    private String tipo;

    @Positive(message = "O tamanho do cabo precisa ser maior que 0!")
    private double tamanhoMetro;

    private Cabo(String idQrCode, String nome, String tipo, double tamanhoMetro, StatusEquipamento status ){
        super(idQrCode, nome, status);
    }
}
