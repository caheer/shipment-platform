package com.example.pdfservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentStatusChangedConsumer {

    @KafkaListener(
            topics = "shipment-status-changed",
            groupId = "pdf-service"
    )
    public void consume(ShipmentStatusChangedEvent event) {

        System.out.println(
                "PDF SERVICE RECEIVED EVENT: " + event
        );
    }
}