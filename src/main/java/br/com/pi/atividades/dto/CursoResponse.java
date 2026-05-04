package br.com.pi.atividades.dto;

public record CursoResponse(
        Integer id,
        String nome,
        Integer cargaHorariaTotal,
        Integer totalAlunos,
        String status
) {}
