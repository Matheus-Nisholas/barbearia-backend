package com.agendamento.barbearia_api.controller;

import com.agendamento.barbearia_api.domain.Usuario;
import com.agendamento.barbearia_api.repository.UsuarioRepository;
import com.agendamento.barbearia_api.domain.user.UserRole;
import com.agendamento.barbearia_api.dto.LoginDTO;
import com.agendamento.barbearia_api.dto.RegisterDTO;
import com.agendamento.barbearia_api.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    /**
     * Endpoint para fazer login.
     * Recebe um login e senha, e se forem válidos, retorna um Token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(token);
    }

    /**
     * Endpoint para cadastrar um novo usuário.
     * Recebe um login e senha, criptografa a senha e salva no banco.
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterDTO data) {

        String password = data.password();

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";

        if (password == null || !password.matches(passwordPattern)) {
            String mensagemErro = "A senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um caractere especial (@#$%^&+=!).";

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemErro);
        }

        if (this.repository.findByLogin(data.login()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este login já está em uso.");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());


        Usuario newUser = new Usuario(data.nome(), data.telefone(), data.login(), encryptedPassword, UserRole.USER);

        this.repository.save(newUser);

        return ResponseEntity.ok().body("Usuário cadastrado com sucesso!");
    }
}