package com.agendamento.barbearia_api.controller;

import com.agendamento.barbearia_api.domain.Servico;
import com.agendamento.barbearia_api.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@CrossOrigin
public class ServicoController {

    @Autowired
    private ServicoRepository repository;

    @GetMapping
    public List<Servico> listarServicos() {
        return repository.findAll();
    }
}