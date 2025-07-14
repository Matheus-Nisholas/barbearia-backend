package com.agendamento.barbearia_api.controller;

import com.agendamento.barbearia_api.domain.Agendamento;
import com.agendamento.barbearia_api.domain.Usuario;
import com.agendamento.barbearia_api.domain.Servico;
import com.agendamento.barbearia_api.dto.AgendamentoRequestDTO;
import com.agendamento.barbearia_api.repository.AgendamentoRepository;
import com.agendamento.barbearia_api.repository.ServicoRepository;
import com.agendamento.barbearia_api.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    private final WhatsAppService whatsAppService;

    @Autowired
    public AgendamentoController(AgendamentoRepository agendamentoRepository, ServicoRepository servicoRepository, WhatsAppService whatsAppService) {
        this.agendamentoRepository = agendamentoRepository;
        this.servicoRepository = servicoRepository;
        this.whatsAppService = whatsAppService;
    }

    @GetMapping
    public ResponseEntity<List<Agendamento>> getAgendamentos(@RequestParam(required = false) LocalDate data) {
        List<Agendamento> agendamentos;
        if (data != null) {

            agendamentos = agendamentoRepository.findAllByData(data);
        } else {

            agendamentos = agendamentoRepository.findAll();
        }
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/faturamento")
    public ResponseEntity<BigDecimal> getFaturamentoDoDia(@RequestParam LocalDate data) {
        List<Agendamento> agendamentosDoDia = agendamentoRepository.findByData(data);

        BigDecimal faturamentoTotal = agendamentosDoDia.stream()
                .map(agendamento -> agendamento.getServico().getPreco())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(faturamentoTotal);
    }

    @GetMapping("/horarios-disponiveis")
    public List<String> getHorariosDisponiveis() {
        return List.of(
                "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00",
                "16:00 - 17:00", "17:00 - 18:00",
                "18:00 - 19:00", "19:00 - 20:00"
        );
    }

    @GetMapping("/ocupados")
    public List<String> getHorariosOcupadosPorData(@RequestParam("data") LocalDate data) {
        return agendamentoRepository.findByData(data)
                .stream()
                .map(Agendamento::getHora)
                .toList();
    }

    @PostMapping
    public ResponseEntity<Object> criarAgendamento(@RequestBody AgendamentoRequestDTO dadosAgendamento, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        LocalDate dataDoAgendamento = dadosAgendamento.data();
        DayOfWeek diaDaSemana = dataDoAgendamento.getDayOfWeek();
        if (diaDaSemana == DayOfWeek.SUNDAY || diaDaSemana == DayOfWeek.MONDAY) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não funcionamos aos domingos e segundas-feiras.");
        }

        Servico servicoSelecionado = servicoRepository.findByNome(dadosAgendamento.servico());
        if (servicoSelecionado == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Serviço não encontrado.");
        }


        boolean horarioOcupado = agendamentoRepository.findByDataAndHora(dadosAgendamento.data(), dadosAgendamento.hora()).isPresent();
        if (horarioOcupado) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Este horário já está agendado.");
        }

        Agendamento novoAgendamento = new Agendamento();
        novoAgendamento.setUsuario(usuarioLogado);
        novoAgendamento.setNome(usuarioLogado.getNome());
        novoAgendamento.setTelefone(usuarioLogado.getTelefone());
        novoAgendamento.setData(dadosAgendamento.data());
        novoAgendamento.setHora(dadosAgendamento.hora());
        novoAgendamento.setServico(servicoSelecionado);

        Agendamento agendamentoSalvo = agendamentoRepository.save(novoAgendamento);

        // Lógica de notificação via WhatsApp (simulada)
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = agendamentoSalvo.getData().format(formatter);
            String telefoneLimpo = agendamentoSalvo.getTelefone().replaceAll("[^0-9]", "");
            whatsAppService.enviarConfirmacao(telefoneLimpo, agendamentoSalvo.getNome(), dataFormatada, agendamentoSalvo.getHora());
        } catch (Exception e) {
            System.err.println("AVISO: O agendamento foi salvo, mas a notificação via WhatsApp falhou: " + e.getMessage());
        }

        return ResponseEntity.ok(agendamentoSalvo);
    }
}