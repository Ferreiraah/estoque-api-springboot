package com.controleestoque.estoque_api.exception;

public class EquipamentoNaoEncontradoException extends RuntimeException {

    public EquipamentoNaoEncontradoException(String idQrCode){
        super("Operação falhou. O Equipamento com QRCode '" + idQrCode + "' não existe no galpão ");
    }

}
