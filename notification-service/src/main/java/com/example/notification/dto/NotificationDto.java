package com.example.notification.dto;
import java.time.LocalDateTime;
import java.util.UUID;
public record NotificationDto(String eventKey, String eventType, UUID referenceId,
                              String message, LocalDateTime createdAt) {}
