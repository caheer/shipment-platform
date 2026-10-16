package com.example.shipment.controller;

import com.example.shipment.dto.ShipmentDetailsDto;
import com.example.shipment.dto.ShipmentListDto;
import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentStatus;
import com.example.shipment.service.ShipmentService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shipment")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping("/createNewShipment")
    public String createNewShipment(@RequestBody Shipment shipment) {

        if (shipment.getShipmentName() == null) {
            return "Shipment name can't be empty";
        }
        Shipment newShipment = shipmentService.createNewShipment(shipment);
        return "shipment/created, no: " + newShipment.getShipmentId();
    }

    @PatchMapping("/updateShipmentStatus/{shipmentId}")
    public String updateShipmentStatus(
            @PathVariable UUID shipmentId,
            @RequestBody ShipmentStatus shipmentStatus) {

        String statusUpdateResult =
                shipmentService.updateShipmentStatus(shipmentId, shipmentStatus);

        return statusUpdateResult;
    }

    @GetMapping("/getShipmentListPage")
    public Page<ShipmentListDto> getShipmentListPage(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return shipmentService.getShipmentListPage(page, size);
    }

    @GetMapping("/searchShipmentsByCriteria")
    public void searchShipmentsByCriteria() {
        shipmentService.searchShipmentsByCriteria();
    }

    @GetMapping("/getShipmentById/{shipmentId}")
    public ShipmentDetailsDto getShipmentById(@PathVariable UUID shipmentId) {
        return shipmentService.getShipmentById(shipmentId);
    }
}