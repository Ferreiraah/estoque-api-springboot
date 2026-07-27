package com.controleestoque.estoque_api.repository;

import com.controleestoque.estoque_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring Boot e inteligente o suficiente para montar o SQL ("SELECT * FROM usuarios WHERE login = ?")
    // só de ler o nome do metodo "findByLogin"
    UserDetails findByLogin(String login);

}
