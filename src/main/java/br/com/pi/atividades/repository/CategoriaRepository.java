package br.com.pi.atividades.repository;

import br.com.pi.atividades.dto.CategoriaResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoriaRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoriaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CategoriaResponse> listar() {
        String sql = """
                SELECT id_categoria AS id,
                       nome,
                       descricao
                FROM categoria_atividade
                ORDER BY nome
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new CategoriaResponse(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descricao")
        ));
    }
}
