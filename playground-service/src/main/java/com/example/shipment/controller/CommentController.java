package com.example.shipment.controller;

import com.example.shipment.model.comment.ShipmentComment;
import com.example.shipment.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping(path = "/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(path = "/addShipmentComment/{shipmentId}")
    public void addNewShipmentComment(@PathVariable UUID shipmentId, @RequestBody ShipmentComment shipmentComment) {
        commentService.addNewShipmentComment(shipmentId, shipmentComment);
    }

    @DeleteMapping(path = "/deleteShipmentComment/{commentId}")
    public void deleteShipmentComment(@PathVariable UUID commentId) {
        commentService.deleteShipmentComment(commentId);

    }
}
