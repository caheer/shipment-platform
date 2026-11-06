package com.example.notification.repository;

import com.example.notification.dto.NotificationDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public class NotificationRepository {
    private final JdbcTemplate jdbc;
    public NotificationRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void saveIfAbsent(String eventKey, String eventType, UUID referenceId, String message) {
        jdbc.update("""
            INSERT INTO notifications (event_key, event_type, reference_id, message)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (event_key) DO NOTHING
            """, eventKey, eventType, referenceId, message);
    }

    public List<NotificationDto> findRecent() {
        return jdbc.query("""
            SELECT event_key, event_type, reference_id, message, created_at
            FROM notifications ORDER BY created_at DESC, event_key LIMIT 100
            """, (rs, row) -> new NotificationDto(
                rs.getString("event_key"), rs.getString("event_type"),
                rs.getObject("reference_id", UUID.class), rs.getString("message"),
                rs.getTimestamp("created_at").toLocalDateTime()));
    }
}
