package com.example.shipment.model.comment;

import com.example.shipment.model.Customer;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers_comments")
public class CustomerComment extends Comment {

    protected CustomerComment() {
    }

    public CustomerComment(UUID commentId, String commentText, LocalDateTime commentDate, String addedBy, Customer customer) {
        super(commentId, commentText, commentDate, addedBy);
        this.customer = customer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}