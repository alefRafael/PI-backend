package br.com.pi.atividades.controller;

import br.com.pi.atividades.dto.AlunoRequest;
import br.com.pi.atividades.dto.AlunoResponse;
import br.com.pi.atividades.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public List<AlunoResponse> listar() {
        return alunoService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlunoResponse criar(@RequestBody @Valid AlunoRequest request) {
        return alunoService.criar(request);
    }

    @PutMapping("/{id}")
    public AlunoResponse atualizar(@PathVariable Integer id, @RequestBody @Valid AlunoRequest request) {
        return alunoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) {
        alunoService.excluir(id);
    }
}
