package com.example.shipment.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @Column(name = "location_id")
    private UUID locationId;

    @Column(name = "location_name", length = 255, nullable = false)
    private String locationName;
}