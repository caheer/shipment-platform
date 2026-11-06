package com.example.pdf;

import com.example.pdf.dto.PdfJobDto;
import com.example.pdf.model.PdfJob;
import com.example.pdf.repository.PdfJobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

// Same class name as before, but persistence is now PostgreSQL instead of a ConcurrentHashMap.
@Service
public class PdfJobRegistry {
    private final PdfJobRepository repository;

    public PdfJobRegistry(PdfJobRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void createJob(UUID jobId, UUID shipmentId) {
        repository.save(new PdfJob(jobId, shipmentId));
    }

    @Transactional
    public void markRunning(UUID jobId) {
        require(jobId).markRunning();
    }

    @Transactional
    public void markCompleted(UUID jobId, String path) {
        require(jobId).markCompleted(path);
    }

    @Transactional
    public void markFailed(UUID jobId, Throwable failure) {
        String message = failure == null ? "Unknown PDF generation failure" : failure.toString();
        require(jobId).markFailed(message.substring(0, Math.min(message.length(), 2000)));
    }

    @Transactional(readOnly = true)
    public PdfJobStatus getStatus(UUID jobId) {
        return require(jobId).getStatus();
    }

    @Transactional(readOnly = true)
    public PdfJobDto getJob(UUID jobId) {
        return PdfJobDto.from(require(jobId));
    }

    @Transactional(readOnly = true)
    public List<PdfJobDto> findByShipmentId(UUID shipmentId) {
        return repository.findByShipmentIdOrderByCreatedAtDesc(shipmentId).stream()
                .map(PdfJobDto::from).toList();
    }

    private PdfJob require(UUID jobId) {
        return repository.findById(jobId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "PDF job not found: " + jobId));
    }
}
