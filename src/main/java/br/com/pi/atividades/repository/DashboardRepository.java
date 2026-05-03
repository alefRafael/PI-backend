package br.com.pi.atividades.repository;

import br.com.pi.atividades.dto.DashboardResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardResponse buscarMetricas() {
        Integer pendentes = valorInteiro("SELECT COUNT(*) FROM atividade WHERE LOWER(status) = 'pendente'");
        Integer totalAlunos = valorInteiro("SELECT COUNT(*) FROM aluno");
        Integer cursosAtivos = valorInteiro("SELECT COUNT(*) FROM curso");
        Integer totalAtividades = valorInteiro("SELECT COUNT(*) FROM atividade");
        Integer aprovadas = valorInteiro("SELECT COUNT(*) FROM atividade WHERE LOWER(status) = 'aprovado'");

        int taxaAprovacao = totalAtividades == 0 ? 0 : (int) Math.round((aprovadas * 100.0) / totalAtividades);

        return new DashboardResponse(pendentes, totalAlunos, cursosAtivos, taxaAprovacao);
    }

    private Integer valorInteiro(String sql) {
        Integer valor = jdbcTemplate.queryForObject(sql, Integer.class);
        return valor == null ? 0 : valor;
    }
}
