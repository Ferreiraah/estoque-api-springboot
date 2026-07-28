package com.controleestoque.estoque_api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.controleestoque.estoque_api.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("estoque-api") // Quem esta emitindo o token
                    .withSubject(usuario.getLogin())  //Quem e o dono do token
                    .withExpiresAt(genExpirationDate()) // Quando o token vence
                    .sign(algorithm);
        } catch (JWTCreationException exception) {

            throw new RuntimeException("Erro ao gerar token jwt", exception);
        }
    }

    // O token vai durar 2 horas. O fuso horário -03:00 garante que pegue o horário de Brasília.
    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }


    public String validateToken(String token) {
        try {
            // Verifica se a variável 'secret' tem o mesmo nome da que você usou no generateToken
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("estoque-api") // Tem que ser exatamente igual ao issuer que está no generateToken
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }
}
