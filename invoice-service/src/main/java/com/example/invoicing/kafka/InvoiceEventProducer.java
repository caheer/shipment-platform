// Put in invoice-service/src/main/java/com/example/invoicing/kafka/
package com.example.invoicing.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InvoiceEventProducer {
    private final KafkaTemplate<String, Object> kafka;
    public InvoiceEventProducer(KafkaTemplate<String, Object> kafka) { this.kafka = kafka; }

    public void publish(InvoiceCreatedEvent event) {
        kafka.send("invoice-created", event.shipmentId().toString(), event);
    }
}
