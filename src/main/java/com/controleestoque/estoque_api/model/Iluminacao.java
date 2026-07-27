package com.controleestoque.estoque_api.model;

import com.controleestoque.estoque_api.enums.CategoriaEquipamento;
import com.controleestoque.estoque_api.enums.StatusEquipamento;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Iluminacao extends Equipamento {

    public Iluminacao(String idQrCode, String nome, StatusEquipamento status, String imagemUrl, CategoriaEquipamento categoria) {
        super(idQrCode, nome, status, categoria, imagemUrl);
    }
}
