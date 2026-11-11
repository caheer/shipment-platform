package com.example.shipment.service;

import com.example.shipment.exception.CustomerNotFoundException;
import com.example.shipment.exception.ShipmentNotFoundException;
import com.example.shipment.model.Customer;
import com.example.shipment.model.Shipment;
import com.example.shipment.repository.CustomerRepository;
import com.example.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private ShipmentService shipmentService;

    @Test
    void shouldUpdateShipmentCustomer() {

        // Arrange
        UUID shipmentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Shipment shipment = new Shipment(shipmentId);
        Customer customer = new Customer(customerId);

        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.of(shipment));

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        // Act
        shipmentService.updateShipmentCustomer(shipmentId, customerId);

        // Assert
        assertEquals(customer, shipment.getCustomer());
    }

    @Test
    void shouldThrowExceptionWhenShipmentDoesNotExist() {

        // Arrange
        UUID shipmentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ShipmentNotFoundException.class,
                () -> shipmentService.updateShipmentCustomer(
                        shipmentId, customerId)
        );
    }


    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {

        UUID shipmentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Shipment shipment = new Shipment(shipmentId);
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> shipmentService.updateShipmentCustomer(shipmentId, customerId));

        // 1. Configure shipmentRepository to return shipment

        // 2. Configure customerRepository to return Optional.empty()

        // 3. Use assertThrows() to verify
        //    CustomerNotFoundException
    }
}
/*

@Transactional
public void updateShipmentCustomer(UUID shipmentId, UUID customerId) {

    Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() ->
                    new ShipmentNotFoundException(shipmentId));

    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() ->
                    new CustomerNotFoundException(customerId));

    shipment.setCustomer(customer);
}
*/
