package com.example.shipment.model;

import com.example.shipment.model.comment.CustomerComment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "customers")
@Entity
@Getter
@Setter
public class Customer {

    public Customer(UUID customerId) {
        this.customerId = customerId;
    }

    protected Customer() {
    }

    @Column(name = "customer_id")
    @Id
    private UUID customerId;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "customer_email", unique = true, nullable = false, length = 255)
    private String customerEmail;

    @OneToMany(mappedBy = "customer")
    private List<CustomerComment> customerComments = new ArrayList<>();

    // relationship can be bidirectional, but it's not a must, depends on business requirement
    // we shouldn't make everything bidirectional, just because we can
    // we use mappedBy here
    /*    @OneToMany(mappedBy = "customer")
    private Set<Shipment> shipments = new HashSet<>();*
     */
}
