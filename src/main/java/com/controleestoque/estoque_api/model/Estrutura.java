package com.controleestoque.estoque_api.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Estrutura extends Equipamento {

    private String material;
    private double dimensaoMetros;

    private Estrutura(String idQrCode, String nome, String material, double dimensaoMetros, StatusEquipamento status, String imagemUrl ) {
        super(idQrCode, nome, status, imagemUrl);
        this.material = material;
        this.dimensaoMetros = dimensaoMetros;
    }

}
