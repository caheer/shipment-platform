package com.example.report.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentStatusChangedConsumer {

    @KafkaListener(
            topics = "shipment-status-changed",
            groupId = "report-service"
    )
    public void consume(ShipmentStatusChangedEvent event) {

        System.out.println(
                "REPORT SERVICE RECEIVED EVENT: " + event
        );
    }
}