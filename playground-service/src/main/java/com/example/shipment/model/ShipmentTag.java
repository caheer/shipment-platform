package com.example.shipment.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "shipment_tags",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_shipment_tags_shipment_tag",
                        columnNames = {"shipment_id", "tag_code"}
                )
        }
)
public class ShipmentTag {

    @Id
    @Column(name = "shipment_tag_id")
    private UUID shipmentTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_code", nullable = false)
    private Tag tag;
}



