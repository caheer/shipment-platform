package com.example.shipment.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "locations")
@Getter
public class Location {

    @Id
    @Column(name = "location_id")
    private UUID locationId;

    @Column(name = "location_name", length = 255, nullable = false)
    private String locationName;
}