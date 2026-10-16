package com.example.shipment.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TestKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TestKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String message) {
        kafkaTemplate.send("test-topic", message);
    }
}