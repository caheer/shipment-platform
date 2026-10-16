package com.example.shipment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Table(name = "couriers")
@Entity
public class Courier {
    public Courier() {
    }

    public Courier(UUID courierId) {
        this.courierId = courierId;
    }

    @Column(name = "courier_id")
    @Id
    private UUID courierId;

    @Column(name = "courier_name", length = 100, nullable = false)
    private String courierName;

    @Column(name = "courier_phone_number", length = 30)
    private String courierPhoneNumber;  // maybe numeric?
}