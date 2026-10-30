package com.example.shipment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentCommentDto(
        UUID commentId,
        String commentText,
        LocalDateTime commentDate,
        String addedBy,
        boolean priorityAction
) {
}