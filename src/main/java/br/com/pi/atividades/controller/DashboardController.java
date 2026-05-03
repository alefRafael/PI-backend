package br.com.pi.atividades.controller;

import br.com.pi.atividades.dto.DashboardResponse;
import br.com.pi.atividades.repository.DashboardRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardRepository dashboardRepository;

    public DashboardController(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @GetMapping
    public DashboardResponse buscarMetricas() {
        return dashboardRepository.buscarMetricas();
    }
}
