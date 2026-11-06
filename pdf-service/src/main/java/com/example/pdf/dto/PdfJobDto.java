package com.example.pdf.dto;

import com.example.pdf.PdfJobStatus;
import com.example.pdf.model.PdfJob;
import java.time.LocalDateTime;
import java.util.UUID;

public record PdfJobDto(
        UUID jobId,
        UUID shipmentId,
        PdfJobStatus status,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        String filePath,
        String errorMessage
) {
    public static PdfJobDto from(PdfJob job) {
        return new PdfJobDto(job.getJobId(), job.getShipmentId(), job.getStatus(),
                job.getCreatedAt(), job.getStartedAt(), job.getFinishedAt(),
                job.getFilePath(), job.getErrorMessage());
    }
}
