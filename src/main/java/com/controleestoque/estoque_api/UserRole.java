package com.controleestoque.estoque_api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    GERENTE("GERENTE"),
    TECNICO("TECNICO");

    private final String role;
}
