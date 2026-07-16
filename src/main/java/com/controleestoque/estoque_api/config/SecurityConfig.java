package com.controleestoque.estoque_api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //1 - O filtro da porta
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desliga a proteção para conseguirmos usar o POST/PUT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Manual liberado

                        // REGRAS DO CRACHÁ:
                        // TÉCNICOS E GERENTES: Podem consultar as listas (Metodo GET)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/**").hasAnyRole("GERENTE", "TECNICO")

                        // SÓ GERENTE: Pode adicionar, alterar e deletar (POST, PUT, DELETE, PATCH)
                        .requestMatchers("/api/**").hasRole("GERENTE")

                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {});

        return http.build();
    }

    //2 - Crachas (Usuarios Provisorios)

    @Bean
    public UserDetailsService users(){
        UserDetails chefe = User.builder()
                .username("pitoco")
                .password("{noop}senha123")
                .roles("GERENTE")
                .build();

        UserDetails tecnico = User.builder()
                .username("freela")
                .password("{noop}montagem")
                .roles("TECNICO")
                .build();

                return new InMemoryUserDetailsManager(chefe, tecnico);
    }
}
