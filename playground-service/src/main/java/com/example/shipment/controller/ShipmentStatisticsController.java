package com.example.shipment.controller;

import com.example.shipment.dto.ShipmentStatusCountDto;
import com.example.shipment.service.ShipmentStatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/statistics")
public class ShipmentStatisticsController {

    private final ShipmentStatisticsService shipmentStatisticsService;

    public ShipmentStatisticsController(ShipmentStatisticsService shipmentStatisticsService) {
        this.shipmentStatisticsService = shipmentStatisticsService;
    }

    @GetMapping(path = "/getShipmentsCountByStatus")
    public List<ShipmentStatusCountDto> getShipmentStatusCount() {

        return shipmentStatisticsService.getShipmentsStatusCount();
    }
}
