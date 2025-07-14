package com.agendamento.barbearia_api.service;

import com.agendamento.barbearia_api.domain.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Gera um novo Token JWT para o usuário fornecido.
     * Inclui informações extras (claims) como o nome e o papel (role) do usuário.
     */
    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("barbearia-api")
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(gerarDataDeExpiracao())
                    .withClaim("nome", usuario.getNome())
                    .withClaim("role", usuario.getRole().toString())
                    .sign(algoritmo);
            return token;
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("barbearia-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }

    private Instant gerarDataDeExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}