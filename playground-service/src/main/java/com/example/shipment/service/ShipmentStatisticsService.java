package com.example.shipment.service;

import com.example.shipment.dto.ShipmentStatusCountDto;
import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentStatus;
import com.example.shipment.repository.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShipmentStatisticsService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentStatisticsService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }


    @Transactional
    public List<ShipmentStatusCountDto> getShipmentsStatusCount() {
        List<Shipment> shipmentList = shipmentRepository.findAll();
        if (shipmentList.isEmpty()) {
            return List.of();
        } else {
            Map<ShipmentStatus, Long> shipmentCountByStatus = shipmentList.stream().collect(Collectors.groupingBy(shipment -> shipment.getShipmentStatus(), Collectors.counting()));
            List<ShipmentStatusCountDto> shipmentStatusCountDtos = shipmentCountByStatus.entrySet().stream().map(entry -> new ShipmentStatusCountDto(entry.getKey(), entry.getValue().longValue())).toList();
            return shipmentStatusCountDtos;

        }
    }
}
/*
OTHER IDEAS FOR PRACTICE:
Shipments by status
1.Map<ShipmentStatus, Long> or Map<ShipmentStatus, List<Shipment>>
Good for groupingBy, counting, EnumMap, Map.

2.Total declared value by status
e.g. CREATED -> 12_000, IN_TRANSIT -> 45_000
Good for groupingBy, reducing, BigDecimal.

Average shipment weight by status
Good for filtering out null, mapping values, grouping.

Shipments by customer
Map<UUID, List<Shipment>> or customer name → list/count.
        Good for grouping by nested properties and dealing with nullable customers.

Shipments assigned to each courier
Map<UUID, List<Shipment>> or courier → count.
        Good if you later initialize/fetch couriers for the stats use case.

        3.Highest-value shipment / heaviest shipment
Returns Optional<Shipment>.
Nice practice for max(), Comparator, Optional.

        4.Top N most valuable shipments
Good for sorted(), Comparator, limit().

Shipments with missing information
        For example, shipments with no customer, no destination, or no courier.
Good for filter, predicates, anyMatch/allMatch.

Priority comments count per shipment
Later, if comments are already loaded appropriately.
        Good for flatMap, filtering, grouping.*/
