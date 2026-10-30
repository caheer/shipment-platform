package com.example.shipment.repository;

import com.example.shipment.model.comment.ShipmentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShipmentCommentRepository extends JpaRepository<ShipmentComment, UUID> {

    List<ShipmentComment> findByShipment_ShipmentIdOrderByCommentDateDesc(UUID shipmentId);}