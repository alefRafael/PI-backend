package br.com.pi.atividades.repository;

import br.com.pi.atividades.dto.SolicitacaoResponse;
import br.com.pi.atividades.exception.RecursoNaoEncontradoException;
import br.com.pi.atividades.exception.RegraNegocioException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SolicitacaoRepository {

    private final JdbcTemplate jdbcTemplate;

    public SolicitacaoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SolicitacaoResponse> solicitacaoMapper = (rs, rowNum) -> new SolicitacaoResponse(
            rs.getInt("id"),
            rs.getString("aluno"),
            rs.getString("atividade"),
            rs.getString("categoria"),
            rs.getInt("horas"),
            rs.getDate("data") == null ? null : rs.getDate("data").toLocalDate(),
            rs.getString("status"),
            rs.getString("observacao")
    );

    public List<SolicitacaoResponse> listar(String status) {
        String baseSql = """
                SELECT atv.id_atividade AS id,
                       u.nome AS aluno,
                       atv.titulo AS atividade,
                       cat.nome AS categoria,
                       atv.carga_horaria AS horas,
                       atv.data_envio AS data,
                       atv.status AS status,
                       av.observacao AS observacao
                FROM atividade atv
                INNER JOIN aluno a ON a.id_usuario = atv.id_aluno
                INNER JOIN usuario u ON u.id = a.id_usuario
                INNER JOIN categoria_atividade cat ON cat.id_categoria = atv.id_categoria
                LEFT JOIN avaliacao av ON av.id_atividade = atv.id_atividade
                """;

        if (status == null || status.isBlank() || status.equalsIgnoreCase("todos")) {
            return jdbcTemplate.query(baseSql + " ORDER BY atv.data_envio DESC, atv.id_atividade DESC", solicitacaoMapper);
        }

        return jdbcTemplate.query(baseSql + " WHERE LOWER(atv.status) = LOWER(?) ORDER BY atv.data_envio DESC, atv.id_atividade DESC",
                solicitacaoMapper, status);
    }

    public SolicitacaoResponse buscarPorId(Integer id) {
        return listar(null)
                .stream()
                .filter(s -> s.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada."));
    }

    @Transactional
    public SolicitacaoResponse aprovar(Integer id) {
        garantirQueAtividadeExiste(id);
        Integer idCoordenador = buscarPrimeiroCoordenador();

        jdbcTemplate.update("UPDATE atividade SET status = 'Aprovado' WHERE id_atividade = ?", id);

        inserirOuAtualizarAvaliacao(id, idCoordenador, "Aprovado", "Atividade aprovada pela coordenação.");

        return buscarPorId(id);
    }

    @Transactional
    public SolicitacaoResponse rejeitar(Integer id, String observacao) {
        garantirQueAtividadeExiste(id);
        Integer idCoordenador = buscarPrimeiroCoordenador();
        String textoObservacao = (observacao == null || observacao.isBlank())
                ? "Solicitação rejeitada pela coordenação."
                : observacao;

        jdbcTemplate.update("UPDATE atividade SET status = 'Rejeitado' WHERE id_atividade = ?", id);

        inserirOuAtualizarAvaliacao(id, idCoordenador, "Rejeitado", textoObservacao);

        return buscarPorId(id);
    }

    private void garantirQueAtividadeExiste(Integer id) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM atividade WHERE id_atividade = ?",
                Integer.class,
                id
        );

        if (total == null || total == 0) {
            throw new RecursoNaoEncontradoException("Solicitação não encontrada.");
        }
    }

    private Integer buscarPrimeiroCoordenador() {
        List<Integer> coordenadores = jdbcTemplate.query(
                "SELECT id_usuario FROM coordenador ORDER BY id_usuario LIMIT 1",
                (rs, rowNum) -> rs.getInt("id_usuario")
        );

        if (coordenadores.isEmpty()) {
            throw new RegraNegocioException("Cadastre pelo menos um coordenador antes de aprovar ou rejeitar solicitações.");
        }

        return coordenadores.get(0);
    }

    private void inserirOuAtualizarAvaliacao(Integer idAtividade, Integer idCoordenador, String status, String observacao) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM avaliacao WHERE id_atividade = ?",
                Integer.class,
                idAtividade
        );

        if (total != null && total > 0) {
            jdbcTemplate.update(
                    """
                    UPDATE avaliacao
                    SET status_avaliacao = ?, observacao = ?, data_avaliacao = ?, id_coordenador = ?
                    WHERE id_atividade = ?
                    """,
                    status, observacao, LocalDate.now(), idCoordenador, idAtividade
            );
        } else {
            Integer horas = jdbcTemplate.queryForObject(
                    "SELECT carga_horaria FROM atividade WHERE id_atividade = ?",
                    Integer.class,
                    idAtividade
            );

            int horasValidadas = status.equalsIgnoreCase("Aprovado") ? (horas == null ? 0 : horas) : 0;

            jdbcTemplate.update(
                    """
                    INSERT INTO avaliacao
                    (carga_horaria_validada, status_avaliacao, observacao, data_avaliacao, id_atividade, id_coordenador)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    horasValidadas, status, observacao, LocalDate.now(), idAtividade, idCoordenador
            );
        }
    }
}
