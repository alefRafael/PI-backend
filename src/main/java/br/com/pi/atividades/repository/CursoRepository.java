package br.com.pi.atividades.repository;

import br.com.pi.atividades.dto.CursoRequest;
import br.com.pi.atividades.dto.CursoResponse;
import br.com.pi.atividades.exception.RecursoNaoEncontradoException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CursoRepository {

    private final JdbcTemplate jdbcTemplate;

    public CursoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CursoResponse> cursoMapper = (rs, rowNum) -> new CursoResponse(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getInt("cargaHorariaTotal"),
            rs.getInt("totalAlunos"),
            "Ativo"
    );

    public List<CursoResponse> listar() {
        String sql = """
                SELECT c.id_curso AS id,
                       c.nome AS nome,
                       c.carga_horaria_total AS cargaHorariaTotal,
                       COUNT(mc.id_aluno) AS totalAlunos
                FROM curso c
                LEFT JOIN matricula_curso mc ON mc.id_curso = c.id_curso
                GROUP BY c.id_curso, c.nome, c.carga_horaria_total
                ORDER BY c.nome
                """;
        return jdbcTemplate.query(sql, cursoMapper);
    }

    public CursoResponse buscarPorId(Integer id) {
        String sql = """
                SELECT c.id_curso AS id,
                       c.nome AS nome,
                       c.carga_horaria_total AS cargaHorariaTotal,
                       COUNT(mc.id_aluno) AS totalAlunos
                FROM curso c
                LEFT JOIN matricula_curso mc ON mc.id_curso = c.id_curso
                WHERE c.id_curso = ?
                GROUP BY c.id_curso, c.nome, c.carga_horaria_total
                """;

        return jdbcTemplate.query(sql, cursoMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado."));
    }

    public CursoResponse criar(CursoRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO curso (nome, carga_horaria_total) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, request.nome());
            ps.setInt(2, request.cargaHorariaTotal());
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKey().intValue();
        return buscarPorId(id);
    }

    public CursoResponse atualizar(Integer id, CursoRequest request) {
        int linhas = jdbcTemplate.update(
                "UPDATE curso SET nome = ?, carga_horaria_total = ? WHERE id_curso = ?",
                request.nome(), request.cargaHorariaTotal(), id
        );

        if (linhas == 0) {
            throw new RecursoNaoEncontradoException("Curso não encontrado.");
        }

        return buscarPorId(id);
    }

    public void excluir(Integer id) {
        int linhas = jdbcTemplate.update("DELETE FROM curso WHERE id_curso = ?", id);
        if (linhas == 0) {
            throw new RecursoNaoEncontradoException("Curso não encontrado.");
        }
    }
}
