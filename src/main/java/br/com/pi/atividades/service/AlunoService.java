package br.com.pi.atividades.service;

import br.com.pi.atividades.dto.AlunoRequest;
import br.com.pi.atividades.dto.AlunoResponse;
import br.com.pi.atividades.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<AlunoResponse> listar() {
        return alunoRepository.listar();
    }

    public AlunoResponse criar(AlunoRequest request) {
        return alunoRepository.criar(request);
    }

    public AlunoResponse atualizar(Integer id, AlunoRequest request) {
        return alunoRepository.atualizar(id, request);
    }

    public void excluir(Integer id) {
        alunoRepository.excluir(id);
    }
}
