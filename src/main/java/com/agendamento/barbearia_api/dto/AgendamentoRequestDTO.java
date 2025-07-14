package com.agendamento.barbearia_api.dto;

import java.time.LocalDate;

public record AgendamentoRequestDTO(LocalDate data, String hora, String servico) {
}