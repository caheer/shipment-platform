package com.example.shipmentexperiments.temp;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class AbstractShipment {

    protected UUID id;
    protected BigDecimal weight;

    public AbstractShipment(UUID id, BigDecimal weight) {
        this.id = id;
        this.weight = weight;
    }

    public abstract BigDecimal calculatePrice();
}