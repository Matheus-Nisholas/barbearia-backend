package com.agendamento.barbearia_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deveria retornar status 200 OK ao solicitar os horários disponíveis")
    void getHorariosDisponiveis_cenario1() throws Exception {
        mockMvc.perform(get("/api/agendamentos/horarios-disponiveis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Deveria retornar status 403 Forbidden ao tentar listar agendamentos sem autenticação")
    void getAgendamentos_cenario1() throws Exception {
        mockMvc.perform(get("/api/agendamentos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deveria retornar status 200 OK ao listar agendamentos com usuário autenticado")
    @WithMockUser
    void getAgendamentos_cenario2() throws Exception {
        mockMvc.perform(get("/api/agendamentos"))
                .andExpect(status().isOk());
    }
}