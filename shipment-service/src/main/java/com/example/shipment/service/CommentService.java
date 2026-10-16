package com.example.shipment.service;

import com.example.shipment.exception.ShipmentCommentNotFoundException;
import com.example.shipment.exception.ShipmentNotFoundException;
import com.example.shipment.model.Shipment;
import com.example.shipment.model.comment.ShipmentComment;
import com.example.shipment.repository.ShipmentCommentRepository;
import com.example.shipment.repository.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CommentService {

    private final ShipmentCommentRepository shipmentCommentRepository;

    private final ShipmentRepository shipmentRepository;

    public CommentService(ShipmentCommentRepository shipmentCommentRepository, ShipmentRepository shipmentRepository) {
        this.shipmentCommentRepository = shipmentCommentRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public void addNewShipmentComment(UUID shipmentId, ShipmentComment shipmentComment) {
        Shipment shipment = shipmentRepository.findByShipmentId(shipmentId).orElseThrow(() -> new ShipmentNotFoundException(shipmentId));
        shipmentComment.setCommentId(UUID.randomUUID());
        shipmentComment.setCommentDate(LocalDateTime.now());
        shipmentComment.setShipment(shipment);
        shipmentCommentRepository.save(shipmentComment);
    }

    @Transactional
    public void deleteShipmentComment(UUID commentId) {
        ShipmentComment shipmentComment = shipmentCommentRepository.findById(commentId).orElseThrow(() -> new ShipmentCommentNotFoundException(commentId));
        shipmentCommentRepository.delete(shipmentComment);
    }
}
