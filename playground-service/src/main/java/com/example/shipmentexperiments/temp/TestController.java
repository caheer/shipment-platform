package com.example.shipmentexperiments.temp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/runTest")
    public void getShipmentByShipmentId() {

        AbstractShipment shipment = new StandardShipment(UUID.randomUUID(), BigDecimal.TWO);
        shipment.calculatePrice();
        StandardShipment standardShipment = new StandardShipment(UUID.randomUUID(), BigDecimal.ONE);
        standardShipment.calculatePrice();
        Integer number = 10;


        testService.doSomething();
        return;
    }
}
