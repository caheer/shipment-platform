package com.example.shipment.repository;

import com.example.shipment.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByEventCode(Long eventCode);

    Optional<Event> findByEventName(String eventName);
}
