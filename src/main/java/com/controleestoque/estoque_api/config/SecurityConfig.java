package com.controleestoque.estoque_api.config;


import com.controleestoque.estoque_api.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    //1 - O filtro da porta
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Desativa a proteção contra CSRF porque a nossa API é REST e vamos usar Tokens.
                .csrf(csrf -> csrf.disable())

                // Define que a nossa API não vai guardar "sessão" na memória (STATELESS).
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Aqui estão as regras de acesso das rotas.
                .authorizeHttpRequests(authorize -> authorize
                        // Libera geral (permitAll) para qualquer requisição POST nestas duas rotas.
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                        // Para QUALQUER outra rota da API, o usuário precisa estar autenticado (authenticated).
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //2 Bean que define as regras do CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //Nao esquecer de colocar o link real do Front quando for para a producao e substituir esse *
        configuration.setAllowedOrigins(Arrays.asList("*"));

        // Quais métodos HTTP o Front pode usar
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Quais cabeçalhos estão liberados (Importantíssimo liberar o Authorization para o JWT passar)
        configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Aplica essa regra para todas as rotas da API
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


}
