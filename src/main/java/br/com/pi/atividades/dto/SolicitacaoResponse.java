package br.com.pi.atividades.dto;

import java.time.LocalDate;

public record SolicitacaoResponse(
        Integer id,
        String aluno,
        String atividade,
        String categoria,
        Integer horas,
        LocalDate data,
        String status,
        String observacao
) {}
