package com.example.pdf.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record PdfGenerationResult(
        UUID shipmentId,
        String pdfContent,
        LocalDateTime generatedAt
) {}