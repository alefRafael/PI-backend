package br.com.pi.atividades.service;

import br.com.pi.atividades.dto.CursoRequest;
import br.com.pi.atividades.dto.CursoResponse;
import br.com.pi.atividades.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<CursoResponse> listar() {
        return cursoRepository.listar();
    }

    public CursoResponse criar(CursoRequest request) {
        return cursoRepository.criar(request);
    }

    public CursoResponse atualizar(Integer id, CursoRequest request) {
        return cursoRepository.atualizar(id, request);
    }

    public void excluir(Integer id) {
        cursoRepository.excluir(id);
    }
}
