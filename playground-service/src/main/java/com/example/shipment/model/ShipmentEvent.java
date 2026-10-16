package com.example.shipment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "shipment_events")
@Entity
@Getter
@Setter
public class ShipmentEvent {

    public ShipmentEvent() {
    }

    public ShipmentEvent(UUID shipmentEventId, Shipment shipment, Event event,
                         LocalDateTime occurredAt) {
        this.shipmentEventId = shipmentEventId;
        this.shipment = shipment;
        this.event = event;
        this.occurredAt = occurredAt;
    }

    @Id
    @Column(name = "shipment_event_id")
    private UUID shipmentEventId;

    // one shipment can have many events
    @JoinColumn(name = "shipment_id", nullable = false)
    @ManyToOne
    private Shipment shipment;

/*    @Column(name = "shipment_id", nullable = false)
    private UUID shipmentId;*/

    // one event can be used in many shipment events
    @JoinColumn(name = "event_code", nullable = false)
    @ManyToOne
    private Event event;

/*    @Column(name = "event_code", nullable = false)
    private Long eventCode;*/

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}