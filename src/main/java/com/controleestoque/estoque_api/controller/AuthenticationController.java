package com.controleestoque.estoque_api.controller;


import com.controleestoque.estoque_api.dto.LoginResponseDTO;
import com.controleestoque.estoque_api.dto.RegisterDTO;
import com.controleestoque.estoque_api.model.Usuario;
import com.controleestoque.estoque_api.dto.AuthenticationDTO;
import com.controleestoque.estoque_api.repository.UsuarioRepository;
import com.controleestoque.estoque_api.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO data){

        // Verifica se já existe alguém com esse login no banco.
        if (this.repository.findByLogin(data.login()) != null){
            return ResponseEntity.badRequest().build();
        }

        // Pega a senha que chegou limpa e passa no triturador do BCrypt.
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        // Cria o usuario novo ja com a senha criptografada.
        Usuario newUser = new Usuario(data.login(), encryptedPassword, data.role());
        // Salva no banco.
        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationDTO data){
        // Pega o login e senha digitados e transforma no formato que o Spring Security entend.e
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());

        // Vai lá no banco (usando o AuthorizationService que criamos) e verifica se a senha bate.
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // Se a senha bater, gera o token JWT.
        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        // Devolve o token na resposta.
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
