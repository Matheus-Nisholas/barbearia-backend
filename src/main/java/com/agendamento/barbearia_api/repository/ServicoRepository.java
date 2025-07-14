package com.agendamento.barbearia_api.repository;

import com.agendamento.barbearia_api.domain.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    Servico findByNome(String nome);
}