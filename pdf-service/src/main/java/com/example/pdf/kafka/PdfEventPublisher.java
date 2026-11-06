// Put in pdf-service/src/main/java/com/example/pdf/kafka/
package com.example.pdf.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PdfEventPublisher {
    private final KafkaTemplate<String, Object> kafka;
    public PdfEventPublisher(KafkaTemplate<String, Object> kafka) { this.kafka = kafka; }
    public void publish(PdfGeneratedEvent event) {
        kafka.send("pdf-generated", event.shipmentId().toString(), event);
    }
}
