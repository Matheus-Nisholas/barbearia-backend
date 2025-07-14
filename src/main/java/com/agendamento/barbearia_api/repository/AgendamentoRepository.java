package com.agendamento.barbearia_api.repository;
import com.agendamento.barbearia_api.domain.Agendamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    Optional<Agendamento> findByDataAndHora(LocalDate data, String hora);

    List<Agendamento> findByData(LocalDate data);

    List<Agendamento> findByDataAndLembreteEnviadoIsFalse(LocalDate data);

    @Query("SELECT a FROM agendamentos a WHERE a.data = :data")
    List<Agendamento> findAllByData(@Param("data") LocalDate data);

}