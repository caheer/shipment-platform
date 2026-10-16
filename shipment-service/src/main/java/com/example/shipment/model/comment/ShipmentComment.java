package com.example.shipment.model.comment;

import com.example.shipment.model.Shipment;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments_comments")
@Setter
public class ShipmentComment extends Comment {

    protected ShipmentComment() {
    }

    public ShipmentComment(
            UUID commentId,
            String commentText,
            LocalDateTime commentDate,
            String addedBy,
            boolean priorityAction,
            Shipment shipment) {

        super(commentId, commentText, commentDate, addedBy);

        this.priorityAction = priorityAction;
        this.shipment = shipment;
    }

    @Column(name = "priority_action", nullable = false)
    private boolean priorityAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;
}