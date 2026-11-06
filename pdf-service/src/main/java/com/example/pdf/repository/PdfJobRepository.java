package com.example.pdf.repository;

import com.example.pdf.model.PdfJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PdfJobRepository extends JpaRepository<PdfJob, UUID> {
    List<PdfJob> findByShipmentIdOrderByCreatedAtDesc(UUID shipmentId);
}
