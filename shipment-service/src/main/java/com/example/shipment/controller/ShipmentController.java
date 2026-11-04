package com.example.shipment.controller;

import com.example.shipment.dto.*;
import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentStatus;
import com.example.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/shipment")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    //DONE
    @PostMapping
    public ResponseEntity<ShipmentDto> createShipment(
            @Valid @RequestBody ShipmentCreateDto dto) {
        ShipmentDto created = shipmentService.createShipment(dto);
        URI location = URI.create("/getShipmentById/" + created.shipmentId());
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{shipmentId}/status")
    public ResponseEntity<Void> updateShipmentStatus(
            @PathVariable UUID shipmentId,
            @Valid @RequestBody ShipmentStatusUpdateDto dto) {

        shipmentService.updateShipmentStatus(shipmentId, dto.shipmentStatus());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getShipmentById/{shipmentId}")
    public ShipmentDetailsDto getShipmentById(@PathVariable UUID shipmentId) {
        return shipmentService.getShipmentById(shipmentId);
    }

    @GetMapping("/getShipmentPdfDataByShipmentId/{shipmentId}")
    public ShipmentPdfDataDto getShipmentPdfDataByShipmentId(@PathVariable UUID shipmentId) {
        return shipmentService.getShipmentPdfDataByShipmentId(shipmentId);
    }

    // POC for what can be returned to frontend as list of shipments
    @GetMapping("/getShipmentListPage")
    public Page<ShipmentListDto> getShipmentListPage(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return shipmentService.getShipmentListPage(page, size);
    }

    // POC for searching by criteria with JPA Criteria API
    @GetMapping("/searchShipmentsByCriteria")
    public void searchShipmentsByCriteria() {
        shipmentService.searchShipmentsByCriteria();
    }




}