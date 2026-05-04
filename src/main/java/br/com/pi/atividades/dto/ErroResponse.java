package br.com.pi.atividades.dto;

import java.time.LocalDateTime;

public record ErroResponse(
        LocalDateTime dataHora,
        Integer status,
        String erro,
        String mensagem
) {}
