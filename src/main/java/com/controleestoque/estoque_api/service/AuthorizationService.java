package com.controleestoque.estoque_api.service;

import com.controleestoque.estoque_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        // Toda vez que tentarem logar, o Spring chama esse metodo, e nós mandamos ele buscar no seu banco!
        return repository.findByLogin(username);
    }

}
