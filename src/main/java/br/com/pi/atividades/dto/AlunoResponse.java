package br.com.pi.atividades.dto;

public record AlunoResponse(
        Integer id,
        String nome,
        String email,
        String matricula,
        Integer idCurso,
        String curso
) {}
