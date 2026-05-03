package br.com.pi.atividades.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CursoRequest(
        @NotBlank(message = "Nome do curso é obrigatório") String nome,
        @Min(value = 1, message = "Meta de horas deve ser maior que zero") Integer cargaHorariaTotal
) {}
