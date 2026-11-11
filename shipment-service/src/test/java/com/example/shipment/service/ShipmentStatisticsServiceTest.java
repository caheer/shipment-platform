package com.example.shipment.service;

import com.example.shipment.dto.shipment.ShipmentStatusCountDto;
import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentStatus;
import com.example.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentStatisticsServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private ShipmentStatisticsService statisticsService;

    @Test
    void shouldReturnEmptyListWhenNoShipmentsExist() {

        // Arrange
        when(shipmentRepository.findAll())
                .thenReturn(List.of());

        // Act
        var result = statisticsService.getShipmentsStatusCount();

        // Assert
        assertTrue(result.isEmpty());



    }

    @Test
    void shouldCountShipmentsByShipmentStatus(){
        Shipment s1 = new Shipment(UUID.randomUUID());
        s1.setShipmentStatus(ShipmentStatus.CREATED);

        Shipment s2 = new Shipment(UUID.randomUUID());
        s2.setShipmentStatus(ShipmentStatus.CREATED);

        Shipment s3 = new Shipment(UUID.randomUUID());
        s3.setShipmentStatus(ShipmentStatus.DELIVERED);

        when(shipmentRepository.findAll()).thenReturn(List.of(s1, s2, s3));

        List<ShipmentStatusCountDto>  result = statisticsService.getShipmentsStatusCount();

        // Assert
        Map<ShipmentStatus, Long> counts = result.stream()
                .collect(Collectors.toMap(
                        status -> status.status(),status -> status.count()
                ));

        assertEquals(2L, counts.get(ShipmentStatus.CREATED));
        assertEquals(1L, counts.get(ShipmentStatus.DELIVERED));

        verify(shipmentRepository).findAll();
        verify(shipmentRepository, times(1)).findAll();
        verify(shipmentRepository, never()).deleteAll();

        when(shipmentRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());



    }
}