package com.controleestoque.estoque_api.dto;

import com.controleestoque.estoque_api.UserRole;

public record RegisterDTO(String login, String senha, UserRole role) {}
