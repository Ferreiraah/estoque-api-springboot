package com.controleestoque.estoque_api.model;


import com.controleestoque.estoque_api.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Login precisa ser unico. O banco vai dar erro se tentarem criar dois usuarios iguais.
    @Column(unique = true,nullable = false)
    private String login;

    @Column(nullable = false)
    private String senha;

    //Avisa o hibernate para gravar a palavra inteira no banco ao inves de um numero (0 ou 1).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    //--- CONTRATO DO SPRING SECURITY (UserDetails) ---//

    //1 Ele converte o Enum nas "Roles" oficiais que o Seguranca entende.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        if(this.role == UserRole.GERENTE){
            // O Gerente ganha o poder master dele E também os poderes básicos do técnico
            return List.of(new SimpleGrantedAuthority("ROLE_GERENTE"), new SimpleGrantedAuthority("ROLE_TECNICO"));
        }else{
            return List.of(new SimpleGrantedAuthority("ROLE_TECNICO"));
        }

    }

    //2 - Avisa ao Spring qual coluna e a senha
    @Override
    public String getPassword(){
        return senha;
    }

    // 3. Avisa ao Spring qual coluna é o nome de usuário
    @Override
    public String getUsername(){
        return login;
    }

    // 4. Daqui para baixo, são travas de segurança adicionais (contas expiradas, bloqueadas).
    // Vamos deixar tudo como 'true' (liberado) por padrão.
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }


    public Usuario(String login, String senha, UserRole role) {
        this.login = login;
        this.senha = senha;
        this.role = role;
    }



}
