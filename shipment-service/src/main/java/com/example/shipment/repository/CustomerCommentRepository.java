package com.example.shipment.repository;

import com.example.shipment.model.comment.CustomerComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerCommentRepository extends JpaRepository<CustomerComment, UUID> {
}
