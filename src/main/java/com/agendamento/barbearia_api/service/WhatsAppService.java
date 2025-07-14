package com.agendamento.barbearia_api.service;

import com.agendamento.barbearia_api.domain.Agendamento;
import com.agendamento.barbearia_api.repository.AgendamentoRepository;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WhatsAppService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String twilioWhatsAppNumber;

    // Injetando o Repository para poder consultar o banco de dados
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    // Método para a notificação de confirmação
    public void enviarConfirmacao(String telefoneCliente, String nomeCliente, String data, String hora) {
        String numeroDestino = "whatsapp:+55" + telefoneCliente;
        String corpoMensagem = String.format(
                "Olá, %s! Seu agendamento na Barbearia para o dia %s às %s foi confirmado. Mal podemos esperar para te ver!",
                nomeCliente, data, hora
        );

        System.out.println("--- SIMULANDO ENVIO DE WHATSAPP ---");
        System.out.println("PARA: " + numeroDestino);
        System.out.println("MENSAGEM: " + corpoMensagem);
        System.out.println("------------------------------------");

        /*
        try {
            Message message = Message.creator(
                    new PhoneNumber(numeroDestino),
                    new PhoneNumber(twilioWhatsAppNumber),
                    corpoMensagem)
                .create();
            System.out.println("Mensagem enviada com sucesso! SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem pelo WhatsApp: " + e.getMessage());
        }
        */
    }

    // =======================================================================
    // NOVO MÉTODO PARA OS LEMBRETES AGENDADOS
    // =======================================================================
    /**
     * Roda a cada minuto (60000 ms) para verificar se há lembretes a serem enviados.
     */
    @Scheduled(fixedRate = 60000)
    public void verificarEEnviarLembretes() {
        System.out.println("Verificando agendamentos para enviar lembretes... " + LocalDateTime.now());

        LocalDateTime agora = LocalDateTime.now();
        // A janela de tempo para enviar um lembrete (ex: entre 60 e 65 minutos antes)
        LocalDateTime limiteInferior = agora.plusHours(1);
        LocalDateTime limiteSuperior = agora.plusHours(1).plusMinutes(5);

        // Busca no banco todos os agendamentos para HOJE que ainda não receberam lembrete
        List<Agendamento> agendamentosDeHoje = agendamentoRepository.findByDataAndLembreteEnviadoIsFalse(agora.toLocalDate());

        for (Agendamento agendamento : agendamentosDeHoje) {
            // Pega a hora de início do agendamento (ex: "09:00")
            String horaInicioStr = agendamento.getHora().split(" - ")[0];
            LocalDateTime horaAgendamento = agora.toLocalDate().atTime(
                    Integer.parseInt(horaInicioStr.split(":")[0]),
                    Integer.parseInt(horaInicioStr.split(":")[1])
            );

            // Verifica se a hora do agendamento está dentro da nossa janela de lembrete
            if (horaAgendamento.isAfter(limiteInferior) && horaAgendamento.isBefore(limiteSuperior)) {

                String telefoneLimpo = agendamento.getTelefone().replaceAll("[^0-9]", "");
                String corpoLembrete = String.format(
                        "Lembrete: seu horário na Barbearia é hoje às %s! Estamos te esperando.",
                        agendamento.getHora()
                );

                System.out.println("--- SIMULANDO ENVIO DE LEMBRETE WHATSAPP ---");
                System.out.println("PARA: whatsapp:+55" + telefoneLimpo);
                System.out.println("MENSAGEM: " + corpoLembrete);
                System.out.println("------------------------------------------");

                // Marca que o lembrete foi enviado e salva no banco para não enviar de novo
                agendamento.setLembreteEnviado(true);
                agendamentoRepository.save(agendamento);
            }
        }
    }
}