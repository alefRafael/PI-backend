package br.com.pi.atividades.controller;

import br.com.pi.atividades.dto.RejeitarSolicitacaoRequest;
import br.com.pi.atividades.dto.SolicitacaoResponse;
import br.com.pi.atividades.service.SolicitacaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @GetMapping
    public List<SolicitacaoResponse> listar(@RequestParam(required = false) String status) {
        return solicitacaoService.listar(status);
    }

    @PutMapping("/{id}/aprovar")
    public SolicitacaoResponse aprovar(@PathVariable Integer id) {
        return solicitacaoService.aprovar(id);
    }

    @PutMapping("/{id}/rejeitar")
    public SolicitacaoResponse rejeitar(@PathVariable Integer id,
                                        @RequestBody(required = false) RejeitarSolicitacaoRequest request) {
        String observacao = request == null ? null : request.observacao();
        return solicitacaoService.rejeitar(id, observacao);
    }
}
