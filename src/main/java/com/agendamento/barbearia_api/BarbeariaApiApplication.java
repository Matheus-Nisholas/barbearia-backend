package com.agendamento.barbearia_api;

import com.agendamento.barbearia_api.domain.Usuario;
import com.agendamento.barbearia_api.domain.Servico;
import com.agendamento.barbearia_api.domain.user.UserRole;
import com.agendamento.barbearia_api.repository.ServicoRepository;
import com.agendamento.barbearia_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@SpringBootApplication
@EnableScheduling
public class BarbeariaApiApplication implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(BarbeariaApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (this.usuarioRepository.findByLogin("admin") == null) {
            String adminPassword = passwordEncoder.encode("admin");
            Usuario admin = new Usuario("Administrador", "00000000000", "admin", adminPassword, UserRole.ADMIN);
            this.usuarioRepository.save(admin);
            System.out.println(">>> Usuário ADMIN padrão criado com sucesso!");
        }

        if (servicoRepository.count() == 0) {
            servicoRepository.save(new Servico(null, "Corte de Cabelo", new BigDecimal("30.00")));
            servicoRepository.save(new Servico(null, "Barba", new BigDecimal("25.00")));
            servicoRepository.save(new Servico(null, "Corte + Barba", new BigDecimal("50.00")));
            servicoRepository.save(new Servico(null, "Pezinho", new BigDecimal("10.00")));
            System.out.println(">>> Serviços padrão criados com sucesso!");
        }
    }
}