package br.com.pi.atividades.dto;

public record DashboardResponse(
        Integer pendentes,
        Integer totalAlunos,
        Integer cursosAtivos,
        Integer taxaAprovacao
) {}
