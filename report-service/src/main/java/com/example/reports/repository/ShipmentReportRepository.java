package com.example.reports.repository;

import com.example.reports.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ShipmentReportRepository {
    private final JdbcTemplate jdbc;

    public ShipmentReportRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // PostgreSQL UPSERTs make event redelivery harmless and handle either topic arriving first.
    public void onShipmentCreated(ShipmentCreatedEvent event) {
        jdbc.update("""
                INSERT INTO shipment_reports
                  (shipment_id, shipment_name, shipment_status, created_at, status_changed_at)
                VALUES (?, ?, 'CREATED', ?, ?)
                ON CONFLICT (shipment_id) DO UPDATE SET
                  shipment_name = EXCLUDED.shipment_name,
                  created_at = EXCLUDED.created_at,
                  shipment_status = CASE
                    WHEN shipment_reports.status_changed_at <= EXCLUDED.status_changed_at
                    THEN 'CREATED' ELSE shipment_reports.shipment_status END,
                  status_changed_at = GREATEST(shipment_reports.status_changed_at,
                                                EXCLUDED.status_changed_at)
                """, event.shipmentId(), event.shipmentName(),
                event.createdAt(), event.createdAt());
    }

    public void onStatusChanged(ShipmentStatusChangedEvent event) {
        jdbc.update("""
                INSERT INTO shipment_reports
                  (shipment_id, shipment_name, shipment_status, created_at, status_changed_at)
                VALUES (?, NULL, ?, NULL, ?)
                ON CONFLICT (shipment_id) DO UPDATE SET
                  shipment_status = EXCLUDED.shipment_status,
                  status_changed_at = EXCLUDED.status_changed_at
                WHERE shipment_reports.status_changed_at <= EXCLUDED.status_changed_at
                """, event.shipmentId(), event.newStatus(), event.changedAt());
    }

    public List<ShipmentStatusCountDto> countByStatus() {
        return jdbc.query("""
                SELECT shipment_status, COUNT(*) AS shipment_count
                FROM shipment_reports
                GROUP BY shipment_status
                ORDER BY shipment_status
                """, (rs, row) -> new ShipmentStatusCountDto(
                rs.getString("shipment_status"), rs.getLong("shipment_count")));
    }

    public List<ShipmentReportDto> findAll() {
        return jdbc.query("""
                SELECT shipment_id, shipment_name, shipment_status, created_at, status_changed_at
                FROM shipment_reports ORDER BY created_at DESC NULLS LAST, shipment_id
                """, (rs, row) -> {
            Timestamp created = rs.getTimestamp("created_at");
            return new ShipmentReportDto(
                    rs.getObject("shipment_id", UUID.class),
                    rs.getString("shipment_name"),
                    rs.getString("shipment_status"),
                    created == null ? null : created.toLocalDateTime(),
                    rs.getTimestamp("status_changed_at").toLocalDateTime());
        });
    }
}
