package com.example.shipment.controller;

import com.example.shipment.kafka.TestKafkaProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class KafkaTestController {

    private final TestKafkaProducer producer;

    public KafkaTestController(TestKafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/send")
    public void send(@RequestParam String message) {
        producer.send(message);
    }
}