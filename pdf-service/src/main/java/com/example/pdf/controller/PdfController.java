package com.example.pdf.controller;

import com.example.pdf.PdfGenerationCoordinator;
import com.example.pdf.PdfJobRegistry;
import com.example.pdf.PdfJobStatus;
import com.example.pdf.dto.PdfJobDto;
import com.example.pdf.service.S3PdfStorageService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pdf")
public class PdfController {
    private final PdfGenerationCoordinator coordinator;
    private final PdfJobRegistry registry;
    private final S3PdfStorageService s3PdfStorageService;

    public PdfController(PdfGenerationCoordinator coordinator, PdfJobRegistry registry, S3PdfStorageService s3PdfStorageService) {
        this.coordinator = coordinator;
        this.registry = registry;
        this.s3PdfStorageService = s3PdfStorageService;
    }

    @PostMapping("/generatePdfAsync/{shipmentId}")
    public UUID generatePdfAsync(@PathVariable UUID shipmentId) {
        return coordinator.startGenerationAsync(shipmentId);
    }

    // Existing endpoint retained for your current frontend.
    @GetMapping("/status/{jobId}")
    public PdfJobStatus getStatus(@PathVariable UUID jobId) {
        return registry.getStatus(jobId);
    }

    @GetMapping("/jobs/{jobId}")
    public PdfJobDto getJob(@PathVariable UUID jobId) {
        return registry.getJob(jobId);
    }

    @GetMapping("/shipments/{shipmentId}/jobs")
    public List<PdfJobDto> getShipmentJobs(@PathVariable UUID shipmentId) {
        return registry.findByShipmentId(shipmentId);
    }

    @GetMapping("/download/{jobId}")
    public ResponseEntity<byte[]> download(@PathVariable UUID jobId) {
        PdfJobDto job = registry.getJob(jobId);

        if (job.status() != PdfJobStatus.COMPLETED || job.filePath() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "PDF is not ready");
        }

        byte[] pdfBytes = s3PdfStorageService.downloadPdf(job.filePath());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .headers(headers -> headers.setContentDisposition(
                        ContentDisposition.attachment()
                                .filename("shipment-" + job.shipmentId() + ".pdf")
                                .build()))
                .body(pdfBytes);
    }
/*
    @GetMapping("/download/{jobId}")
    public ResponseEntity<Resource> download(@PathVariable UUID jobId) {
        PdfJobDto job = registry.getJob(jobId);
        if (job.status() != PdfJobStatus.COMPLETED || job.filePath() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "PDF is not ready");
        }
        Path file = Path.of(job.filePath());
        if (!Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PDF file is missing");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .headers(headers -> headers.setContentDisposition(
                        ContentDisposition.attachment()
                                .filename(file.getFileName().toString())
                                .build()))
                .body(new FileSystemResource(file));
    }*/
}