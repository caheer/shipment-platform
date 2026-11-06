package com.example.reports.controller;

import com.example.reports.dto.ShipmentReportDto;
import com.example.reports.dto.ShipmentStatusCountDto;
import com.example.reports.repository.ShipmentReportRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {
    private final ShipmentReportRepository repository;

    public ReportsController(ShipmentReportRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/shipments/status-counts")
    public List<ShipmentStatusCountDto> statusCounts() {
        return repository.countByStatus();
    }

    @GetMapping("/shipments")
    public List<ShipmentReportDto> shipments() {
        return repository.findAll();
    }
}
