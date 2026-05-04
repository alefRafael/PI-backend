package br.com.pi.atividades.service;

import br.com.pi.atividades.dto.SolicitacaoResponse;
import br.com.pi.atividades.repository.SolicitacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public List<SolicitacaoResponse> listar(String status) {
        return solicitacaoRepository.listar(status);
    }

    public SolicitacaoResponse aprovar(Integer id) {
        return solicitacaoRepository.aprovar(id);
    }

    public SolicitacaoResponse rejeitar(Integer id, String observacao) {
        return solicitacaoRepository.rejeitar(id, observacao);
    }
}
