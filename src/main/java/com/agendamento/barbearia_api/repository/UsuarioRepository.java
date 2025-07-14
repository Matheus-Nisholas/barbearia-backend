package com.agendamento.barbearia_api.repository;

import com.agendamento.barbearia_api.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

// Esta interface estende JpaRepository, então não precisa da anotação @Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {


    UserDetails findByLogin(String login);

}