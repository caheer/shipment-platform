package com.example.shipment.controller;

import com.example.shipment.dto.shipment.ShipmentDetailsDto;
import com.example.shipment.model.ShipmentStatus;
import com.example.shipment.service.ShipmentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShipmentController.class)
class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShipmentService shipmentService;

    @Test
    void shouldReturnShipmentDetails() throws Exception {

        // Arrange
        UUID shipmentId = UUID.randomUUID();

        ShipmentDetailsDto dto = new ShipmentDetailsDto(
                shipmentId,
                "Laptop",
                ShipmentStatus.CREATED,
                "Handle with care"
        );

        when(shipmentService.getShipmentById(shipmentId))
                .thenReturn(dto);

        // Act + Assert
        mockMvc.perform(get("/shipment/getShipmentById/{shipmentId}",
                        shipmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentName").value("Laptop"))
                .andExpect(jsonPath("$.shipmentStatus").value("CREATED"));
    }
}