package com.controleestoque.estoque_api.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Audio extends Equipamento {

    public Audio(String idQrCode, String nome, StatusEquipamento status) {
        super(idQrCode, nome, status);
    }
}
