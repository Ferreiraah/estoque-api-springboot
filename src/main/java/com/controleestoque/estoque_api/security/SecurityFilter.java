package com.controleestoque.estoque_api.security;


import com.controleestoque.estoque_api.repository.UsuarioRepository;
import com.controleestoque.estoque_api.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);

        // Se a requisição veio com um token, vamos validar!
        if (token != null) {
            var login = tokenService.validateToken(token); // Retorna o login se o token for válido
            UserDetails user = repository.findByLogin(login);

            // Se achou o usuário, avisa o Spring Security que essa requisição está autenticada
            if (user != null) {
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Passa a requisição para frente (seja para dar 403 ou para acessar o Controller)
        filterChain.doFilter(request, response);
    }

    // Pega o token que vem escondido no cabeçalho da requisição
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}

