package br.com.pi.atividades.controller;

import br.com.pi.atividades.dto.CursoRequest;
import br.com.pi.atividades.dto.CursoResponse;
import br.com.pi.atividades.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public List<CursoResponse> listar() {
        return cursoService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CursoResponse criar(@RequestBody @Valid CursoRequest request) {
        return cursoService.criar(request);
    }

    @PutMapping("/{id}")
    public CursoResponse atualizar(@PathVariable Integer id, @RequestBody @Valid CursoRequest request) {
        return cursoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) {
        cursoService.excluir(id);
    }
}
