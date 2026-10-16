package com.example.shipment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "events")
@Entity
@Getter
@Setter
public class Event {
    // data modified in db, no constructor with parameters
    protected Event() {
    }

    // Below would make sense if application had a feature to create dictionary entries

    /*    public Event(Long eventCode, String eventName) {
        this.eventCode = eventCode;
        this.eventName = eventName;
    }*/

    @Column(name = "event_code")
    @Id
    private Long eventCode;

    @Column(name = "event_name", unique = true, length = 100, nullable = false)
    private String eventName;


}
