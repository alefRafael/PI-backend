package br.com.pi.atividades.repository;

import br.com.pi.atividades.dto.AlunoRequest;
import br.com.pi.atividades.dto.AlunoResponse;
import br.com.pi.atividades.exception.RecursoNaoEncontradoException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Repository
public class AlunoRepository {

    private final JdbcTemplate jdbcTemplate;

    public AlunoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AlunoResponse> alunoMapper = (rs, rowNum) -> new AlunoResponse(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("matricula"),
            rs.getObject("idCurso", Integer.class),
            rs.getString("curso")
    );

    public List<AlunoResponse> listar() {
        String sql = """
                SELECT u.id AS id,
                       u.nome AS nome,
                       u.email AS email,
                       a.matricula AS matricula,
                       c.id_curso AS idCurso,
                       c.nome AS curso
                FROM aluno a
                INNER JOIN usuario u ON u.id = a.id_usuario
                LEFT JOIN matricula_curso mc ON mc.id_aluno = a.id_usuario
                LEFT JOIN curso c ON c.id_curso = mc.id_curso
                ORDER BY u.nome
                """;
        return jdbcTemplate.query(sql, alunoMapper);
    }

    public AlunoResponse buscarPorId(Integer id) {
        String sql = """
                SELECT u.id AS id,
                       u.nome AS nome,
                       u.email AS email,
                       a.matricula AS matricula,
                       c.id_curso AS idCurso,
                       c.nome AS curso
                FROM aluno a
                INNER JOIN usuario u ON u.id = a.id_usuario
                LEFT JOIN matricula_curso mc ON mc.id_aluno = a.id_usuario
                LEFT JOIN curso c ON c.id_curso = mc.id_curso
                WHERE u.id = ?
                """;
        return jdbcTemplate.query(sql, alunoMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado."));
    }

    @Transactional
    public AlunoResponse criar(AlunoRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO usuario (email, telefone, perfil, senha, nome) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, request.email());
            ps.setString(2, null);
            ps.setString(3, "ALUNO");
            ps.setString(4, "senha_temporaria");
            ps.setString(5, request.nome());
            return ps;
        }, keyHolder);

        Integer idUsuario = keyHolder.getKey().intValue();

        jdbcTemplate.update(
                "INSERT INTO aluno (id_usuario, matricula, data_ingresso) VALUES (?, ?, ?)",
                idUsuario, request.matricula(), LocalDate.now()
        );

        jdbcTemplate.update(
                "INSERT INTO matricula_curso (id_aluno, id_curso) VALUES (?, ?)",
                idUsuario, request.idCurso()
        );

        return buscarPorId(idUsuario);
    }

    @Transactional
    public AlunoResponse atualizar(Integer id, AlunoRequest request) {
        int usuarioAtualizado = jdbcTemplate.update(
                "UPDATE usuario SET nome = ?, email = ? WHERE id = ?",
                request.nome(), request.email(), id
        );

        if (usuarioAtualizado == 0) {
            throw new RecursoNaoEncontradoException("Aluno não encontrado.");
        }

        jdbcTemplate.update(
                "UPDATE aluno SET matricula = ? WHERE id_usuario = ?",
                request.matricula(), id
        );

        jdbcTemplate.update("DELETE FROM matricula_curso WHERE id_aluno = ?", id);
        jdbcTemplate.update(
                "INSERT INTO matricula_curso (id_aluno, id_curso) VALUES (?, ?)",
                id, request.idCurso()
        );

        return buscarPorId(id);
    }

    @Transactional
    public void excluir(Integer id) {
        int linhas = jdbcTemplate.update("DELETE FROM usuario WHERE id = ?", id);
        if (linhas == 0) {
            throw new RecursoNaoEncontradoException("Aluno não encontrado.");
        }
    }
}
